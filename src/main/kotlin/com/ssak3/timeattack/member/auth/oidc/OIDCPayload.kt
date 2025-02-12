package com.ssak3.timeattack.member.auth.oidc

data class OIDCPayload(
    val sub: String,
    val email: String?,
    val picture: String?,
    val name: String?
)