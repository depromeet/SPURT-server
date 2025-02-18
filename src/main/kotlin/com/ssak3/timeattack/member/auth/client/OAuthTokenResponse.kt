package com.ssak3.timeattack.member.auth.client

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuthTokenResponse (
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("id_token")
    val idToken: String,

    @JsonProperty("expires_in")
    val expiresIn: Int
)
