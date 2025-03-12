package com.ssak3.timeattack.common.security

data class JwtTokenDto(
    val accessToken: String,
    val refreshToken: String,
)
