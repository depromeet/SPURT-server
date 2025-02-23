package com.ssak3.timeattack.member.service

import com.ssak3.timeattack.common.security.JwtTokenDto
import com.ssak3.timeattack.common.security.JwtTokenProvider
import com.ssak3.timeattack.member.auth.client.OAuthClientFactory
import com.ssak3.timeattack.member.auth.oidc.OIDCPayload
import com.ssak3.timeattack.member.auth.oidc.OIDCTokenVerification
import com.ssak3.timeattack.member.controller.LoginRequest
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oAuthClientFactory: OAuthClientFactory,
    private val oidcTokenVerification: OIDCTokenVerification,
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun authenticateAndRegister(request: LoginRequest): JwtTokenDto {
        // id token 요청
        val oAuthClient = oAuthClientFactory.getClient(request.provider)
        val idToken = oAuthClient.getToken(request.authCode).idToken

        // 공개키 요청
        val publicKeys = oAuthClient.getPublicKeys()

        // id token 파싱
        val oidcPayload = oidcTokenVerification.verifyIdToken(idToken, publicKeys)

        // 유저 존재 여부 확인 -> 없으면 유저 생성 (= 자동 회원가입)
        val member =
            memberRepository.findByProviderAndSubject(request.provider, oidcPayload.subject)
                ?.let { Member.toDomain(it) }
                ?: createMember(oidcPayload, request.provider)

        // JWT 토큰 생성 & 반환
        // member 객체 무조건 존재하기에 !! 사용
        return jwtTokenProvider.generateTokens(member.id!!)
    }

    private fun createMember(
        oidcPayload: OIDCPayload,
        provider: OAuthProvider,
    ): Member =
        Member.toDomain(
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
