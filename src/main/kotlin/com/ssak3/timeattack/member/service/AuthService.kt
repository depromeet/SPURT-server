package com.ssak3.timeattack.member.service

import com.ssak3.timeattack.common.domain.DevicePlatform
import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.security.JwtTokenProvider
import com.ssak3.timeattack.common.security.refreshtoken.RefreshTokenService
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.auth.client.OAuthClientFactory
import com.ssak3.timeattack.member.auth.client.OAuthTokenResponse
import com.ssak3.timeattack.member.auth.oidc.OIDCPayload
import com.ssak3.timeattack.member.auth.oidc.OIDCTokenVerification
import com.ssak3.timeattack.member.controller.dto.AppleLoginRequest
import com.ssak3.timeattack.member.controller.dto.LoginRequest
import com.ssak3.timeattack.member.domain.AppleAuthToken
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.AppleAuthTokenRepository
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo
import com.ssak3.timeattack.member.service.dto.LoginResult
import com.ssak3.timeattack.member.service.dto.MemberInfo
import com.ssak3.timeattack.member.service.events.DeviceRegisterEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val oAuthClientFactory: OAuthClientFactory,
    private val oidcTokenVerification: OIDCTokenVerification,
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,
    private val eventPublisher: ApplicationEventPublisher,
    private val appleAuthTokenRepository: AppleAuthTokenRepository,
) : Logger {
    // 카카오 & 구글 소셜 로그인
    @Transactional
    fun authenticateAndRegister(request: LoginRequest): LoginResult {
        // id token 요청
        val oAuthClient = oAuthClientFactory.getClient(request.provider)
        val idToken = oAuthClient.getToken(request.authCode).idToken

        // 공개키 요청
        val publicKeys = oAuthClient.getPublicKeys()

        // id token 파싱
        val oidcPayload = oidcTokenVerification.verifyIdToken(idToken, publicKeys)

        // 유저 존재 여부 확인 -> 없으면 유저 생성 (= 자동 회원가입)
        val (member, isNewUser) = getMemberOrRegister(request.provider, oidcPayload)

        // 공통 로그인 처리: JWT 생성 및 Redis에 refresh token 저장  & 디바이스 정보 저장 이벤트 발행
        return processLogin(member, request.deviceId, request.deviceType, isNewUser)
    }

    // 애플 소셜 로그인
    // TODO: 애플 로그인 테스트 후 로그 삭제하기
    @Transactional
    fun authenticateAndRegister(request: AppleLoginRequest): LoginResult {
        val oAuthClient = oAuthClientFactory.getClient(OAuthProvider.APPLE)
        val oAuthTokenResponse = oAuthClient.getToken(request.authCode)
        logger.info("OAuth Token Response: $oAuthTokenResponse")

        val publicKeys = oAuthClient.getPublicKeys()
        logger.info("Public Keys: $publicKeys")

        val oidcPayload = oidcTokenVerification.verifyIdToken(oAuthTokenResponse.idToken, publicKeys)
        logger.info("OIDC Payload: $oidcPayload")

        val updatedPayload =
            OIDCPayload(
                subject = oidcPayload.subject,
                email = request.email,
                picture = oidcPayload.picture,
                name = request.nickname,
            )

        val (member, isNewUser) = getMemberOrRegister(OAuthProvider.APPLE, updatedPayload)

        checkNotNull(member.id, "memberId")

        // apple oauth refresh token DB에 저장 or 업데이트
        createOrUpdateAppleAuthToken(member.id, isNewUser, oAuthTokenResponse)

        // 공통 로그인 처리
        return processLogin(member, request.deviceId, request.deviceType, isNewUser)
    }

    /**
     * Apple OAuth 인증 토큰을 생성하거나 업데이트
     * 새로운 사용자인 경우 새 AppleAuthToken 생성
     * 기존 사용자인 경우 데이터베이스에서 기존 토큰을 조회하여 refresh token을 업데이트
     * 이 토큰은 나중에 사용자 계정 탈퇴 같은 작업에서 Apple 서비스와 통신할 때 필요
     */
    private fun createOrUpdateAppleAuthToken(
        memberId: Long,
        isNewUser: Boolean,
        oAuthTokenResponse: OAuthTokenResponse,
    ) {
        val appleAuthToken =
            when (isNewUser) {
                true -> {
                    AppleAuthToken(
                        memberId = memberId,
                        refreshToken = oAuthTokenResponse.refreshToken,
                    )
                }
                false -> {
                    val authToken = getAuthToken(memberId)
                    authToken.updateRefreshToken(oAuthTokenResponse.refreshToken)
                    authToken
                }
            }

        logger.info("apple refresh Token: $appleAuthToken")

        appleAuthTokenRepository.save(appleAuthToken.toEntity())
    }

    /**
     * provider, oidc payload로 멤버 조회, 없으면 새 멤버 생성
     */
    private fun getMemberOrRegister(
        provider: OAuthProvider,
        oidcPayload: OIDCPayload,
    ): Pair<Member, Boolean> {
        var isNewUser = false
        val member =
            memberRepository.findByProviderAndSubject(provider, oidcPayload.subject)
                ?.let { Member.fromEntity(it) }
                ?: run {
                    isNewUser = true
                    createMember(oidcPayload, provider)
                }
        return Pair(member, isNewUser)
    }

    // 애플 소셜 로그인 시, DB에서 refresh token 조회
    private fun getAuthToken(id: Long): AppleAuthToken {
        val appleAuthToken =
            AppleAuthToken.fromEntity(
                appleAuthTokenRepository.findById(id)
                    .orElseThrow { ApplicationException(ApplicationExceptionType.AUTH_TOKEN_NOT_FOUND) },
            )
        return appleAuthToken
    }

    /**
     * 공통 로그인 처리
     * JWT 토큰 생성
     * 캐시에 Refresh token 저장
     * 기기 정보 저장 이벤트 발행
     */
    private fun processLogin(
        member: Member,
        deviceId: String,
        deviceType: DevicePlatform,
        isNewUser: Boolean,
    ): LoginResult {
        checkNotNull(member.id, "memberId")

        // spurt 용 jwt token 생성
        val tokens = jwtTokenProvider.generateTokens(member.id)

        // redis에 refresh token 저장
        refreshTokenService.saveRefreshToken(member.id, tokens.refreshToken)

        // 기기 저장 event 발행
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

    fun withdraw(member: Member) {
        val requestedMemberId = checkNotNull(member.id, "memberId")

        // 1. 회원 정보 지우기
        member.delete()
        memberRepository.save(member.toEntity())

        // 2. 카카오 연결 끊기 -> 바뀌는 과정
        when(member.oAuthProviderInfo.oauthProvider) {
            OAuthProvider.KAKAO -> {
                val oAuthClient = oAuthClientFactory.getClient(OAuthProvider.KAKAO)
                oAuthClient.unlink(member.oAuthProviderInfo.subject)
            }
            else -> TODO()
        }

        // 3. refreshToken 지우기
        refreshTokenService.deleteRefreshToken(requestedMemberId)

    }
}
