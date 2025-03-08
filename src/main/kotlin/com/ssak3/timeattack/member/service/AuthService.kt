package com.ssak3.timeattack.member.service

import com.ssak3.timeattack.common.security.JwtTokenProvider
import com.ssak3.timeattack.common.security.refreshtoken.RefreshTokenService
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.member.auth.client.OAuthClientFactory
import com.ssak3.timeattack.member.auth.oidc.OIDCPayload
import com.ssak3.timeattack.member.auth.oidc.OIDCTokenVerification
import com.ssak3.timeattack.member.controller.dto.LoginRequest
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
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
) : Logger {
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

        // JWT 토큰 생성 & 반환
        checkNotNull(member.id) { "Member ID must not be null" }
        val tokens = jwtTokenProvider.generateTokens(member.id)

        // refresh token 저장
        refreshTokenService.saveRefreshToken(member.id, tokens.refreshToken)

        // 기기 정보 저장 이벤트 발행
        eventPublisher.publishEvent(DeviceRegisterEvent(member.id, request.deviceId, request.deviceType))

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
    ): Member =
        Member.fromEntity(
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
