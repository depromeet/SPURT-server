package com.ssak3.timeattack.member.auth.client

import com.ssak3.timeattack.member.domain.OAuthProvider
import org.springframework.stereotype.Component

@Component
class OAuthClientFactory(
    private val kakaoOAuthClient: KakaoOAuthClient,
) {

    fun getClient(provider: OAuthProvider): OAuthClient {
        return when (provider) {
            OAuthProvider.KAKAO -> kakaoOAuthClient
            OAuthProvider.GOOGLE -> TODO()
        }
    }
}