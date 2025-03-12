package com.ssak3.timeattack.member.service

import com.ssak3.timeattack.common.domain.DevicePlatform
import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.security.JwtTokenProvider
import com.ssak3.timeattack.common.security.refreshtoken.RefreshTokenService
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.auth.client.OAuthClientFactory
import com.ssak3.timeattack.member.auth.oidc.OIDCPayload
import com.ssak3.timeattack.member.auth.oidc.OIDCTokenVerification
import com.ssak3.timeattack.member.controller.dto.AppleLoginRequest
import com.ssak3.timeattack.member.controller.dto.LoginRequest
import com.ssak3.timeattack.member.domain.AuthToken
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.AuthTokenRepository
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo
import com.ssak3.timeattack.member.service.dto.LoginResult
import com.ssak3.timeattack.member.service.dto.MemberInfo
import com.ssak3.timeattack.member.service.events.DeviceRegisterEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oAuthClientFactory: OAuthClientFactory,
    private val oidcTokenVerification: OIDCTokenVerification,
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,
    private val eventPublisher: ApplicationEventPublisher,
    private val authTokenRepository: AuthTokenRepository,
) : Logger {
    // 카카오 & 구글 소셜 로그인
    fun authenticateAndRegister(request: LoginRequest): LoginResult {
        // id token 요청
        val oAuthClient = oAuthClientFactory.getClient(request.provider)
        val idToken = oAuthClient.getToken(request.authCode).idToken

        // 공개키 요청
        val publicKeys = oAuthClient.getPublicKeys()

        // id token 파싱
        val oidcPayload = oidcTokenVerification.verifyIdToken(idToken, publicKeys)

        // 유저 존재 여부 확인 -> 없으면 유저 생성 (= 자동 회원가입)
        var isNewUser = false
        val member =
            memberRepository.findByProviderAndSubject(request.provider, oidcPayload.subject)
                ?.let { Member.fromEntity(it) }
                ?: run {
                    isNewUser = true
                    createMember(oidcPayload, request.provider)
                }

        return processLogin(member, request.deviceId, request.deviceType, isNewUser)
    }

    // 애플 소셜 로그인
    fun authenticateAndRegister(request: AppleLoginRequest): LoginResult {
        val oAuthClient = oAuthClientFactory.getClient(OAuthProvider.APPLE)
        val oAuthTokenResponse = oAuthClient.getToken(request.authCode)
        logger.info("OAuth Token Response: $oAuthTokenResponse")

        val publicKeys = oAuthClient.getPublicKeys()
        logger.info("Public Keys: $publicKeys")

        val oidcPayload = oidcTokenVerification.verifyIdToken(oAuthTokenResponse.idToken, publicKeys)
        logger.info("OIDC Payload: $oidcPayload")

        var isNewUser = false
        val member =
            memberRepository.findByProviderAndSubject(OAuthProvider.APPLE, oidcPayload.subject)
                ?.let {
                    Member.fromEntity(it)
                }
                ?: run {
                    isNewUser = true
                    val updatedPayload =
                        OIDCPayload(
                            subject = oidcPayload.subject,
                            email = request.email,
                            picture = oidcPayload.picture,
                            name = request.nickname,
                        )
                    createMember(updatedPayload, OAuthProvider.APPLE)
                }

        checkNotNull(member.id, "memberId")

        // apple refresh token 저장
        val authToken =
            when (isNewUser) {
                true -> {
                    AuthToken(
                        memberId = member.id,
                        refreshToken = oAuthTokenResponse.refreshToken,
                    )
                }
                false -> {
                    val authToken = getAuthToken(member.id)
                    authToken.updateRefreshToken(oAuthTokenResponse.refreshToken)
                    authToken
                }
            }
        logger.info("apple refresh Token: $authToken")
        authTokenRepository.save(authToken.toEntity())

        return processLogin(member, request.deviceId, request.deviceType, isNewUser)
    }

    // 회원 탈퇴
    fun withdraw(member: Member) {
        val requestedMemberId = checkNotNull(member.id, "memberId")

        // 1. 회원 정보 지우기
        member.delete()
        memberRepository.save(member.toEntity())
        logger.info("==================== 회원 정보 삭제 -> id: ${member.id} isDeleted: ${member.isDeleted}")

        // 2. Social 연결 끊기 -> 바뀌는 과정
        when (member.oAuthProviderInfo.oauthProvider) {
            OAuthProvider.KAKAO -> {
                val oAuthClient = oAuthClientFactory.getClient(OAuthProvider.KAKAO)
                oAuthClient.unlink(member.oAuthProviderInfo.subject)
            }
            OAuthProvider.APPLE -> {
                val oAuthClient = oAuthClientFactory.getClient(OAuthProvider.APPLE)
                oAuthClient.unlink(member.id.toString())
            }
            OAuthProvider.GOOGLE -> TODO()
        }

        // 3. refreshToken 지우기
        refreshTokenService.deleteRefreshToken(requestedMemberId)
    }

    // 애플 소셜 로그인 시, DB에서 refresh token 조회
    private fun getAuthToken(id: Long): AuthToken {
        val authToken =
            AuthToken.fromEntity(
                authTokenRepository.findById(id)
                    .orElseThrow { ApplicationException(ApplicationExceptionType.AUTH_TOKEN_NOT_FOUND) },
            )
        return authToken
    }

    // 공통 로그인 처리(JWT 토큰 생성, 캐시에 Refresh token 저장, 기기 정보 저장 이벤트 발행)
    private fun processLogin(
        member: Member,
        deviceId: String,
        deviceType: DevicePlatform,
        isNewUser: Boolean,
    ): LoginResult {
        checkNotNull(member.id) { "Member ID must not be null" }
        val tokens = jwtTokenProvider.generateTokens(member.id)

        refreshTokenService.saveRefreshToken(member.id, tokens.refreshToken)

        eventPublisher.publishEvent(DeviceRegisterEvent(member.id, deviceId, deviceType))

        val loginResult =
            LoginResult(
                tokens,
                isNewUser,
                MemberInfo(member.id, member.nickname, member.email, member.profileImageUrl),
            )
        logger.info("loginResult = $loginResult")
        return loginResult
    }

    private fun createMember(
        oidcPayload: OIDCPayload,
        provider: OAuthProvider,
    ): Member {
        checkNotNull(oidcPayload.name, "nickname")
        checkNotNull(oidcPayload.email, "email")
        return Member.fromEntity(
            memberRepository.save(
                Member(
                    nickname = oidcPayload.name,
                    email = oidcPayload.email,
                    profileImageUrl = oidcPayload.picture,
                    oAuthProviderInfo = OAuthProviderInfo(provider, oidcPayload.subject),
                ).toEntity(),
            ),
        )
    }
}
