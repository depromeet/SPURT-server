package com.ssak3.timeattack.member.auth.client

import com.ssak3.timeattack.member.auth.oidc.OIDCPublicKeyList

interface OAuthClient {
    fun getToken(authCode: String): OAuthTokenResponse

    fun getPublicKeys(): OIDCPublicKeyList
}
