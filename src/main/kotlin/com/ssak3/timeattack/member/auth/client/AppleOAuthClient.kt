package com.ssak3.timeattack.member.auth.client

import com.ssak3.timeattack.member.auth.oidc.OIDCPublicKeyList
import com.ssak3.timeattack.member.auth.properties.AppleProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AppleOAuthClient(
    @Autowired
    val appleFeignClient: AppleFeignClient,
    @Autowired
    val appleProperties: AppleProperties,
) : OAuthClient {
    override fun getToken(authCode: String): OAuthTokenResponse {
        return appleFeignClient.getToken(
            code = authCode,
            clientId = appleProperties.clientId,
            clientSecret = appleProperties.clientSecret,
            redirectUri = appleProperties.redirectUri,
        )
    }

    override fun getPublicKeys(): OIDCPublicKeyList {
        return appleFeignClient.getPublicKeys()
    }
}
