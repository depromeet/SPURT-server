package com.ssak3.timeattack.common.security

data class JwtTokenDto(
    var accessToken: String,
    var refreshToken: String,
)
