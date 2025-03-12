package com.ssak3.timeattack.member.auth.client

import com.ssak3.timeattack.member.auth.oidc.OIDCPublicKeyList
import com.ssak3.timeattack.member.auth.properties.KakaoProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class KakaoOAuthClient(
    @Autowired
    val kakaoOAuthFeignClient: KakaoOAuthFeignClient,
    @Autowired
    val kakaoProperties: KakaoProperties,
) : OAuthClient {
    override fun getToken(authCode: String): OAuthTokenResponse {
        return kakaoOAuthFeignClient.getToken(
            code = authCode,
            clientId = kakaoProperties.clientId,
            clientSecret = kakaoProperties.clientSecret,
            redirectUri = kakaoProperties.redirectUri,
        )
    }

    override fun getPublicKeys(): OIDCPublicKeyList {
        return kakaoOAuthFeignClient.getPublicKeys()
    }
}
