package com.ssak3.timeattack.member.controller

import com.ssak3.timeattack.common.security.JwtTokenDto

data class LoginResponse(
    val jwtTokenDto: JwtTokenDto,
    val isNewUser: Boolean,
)
