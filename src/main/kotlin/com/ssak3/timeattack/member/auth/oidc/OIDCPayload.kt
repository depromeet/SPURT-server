package com.ssak3.timeattack.member.auth.oidc

/**
 * ID 토큰에서 추출한 유저 정보를 담는 data class
 */
data class OIDCPayload(
    val subject: String,
    val email: String,
    val picture: String,
    val name: String,
)
