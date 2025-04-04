package com.ssak3.timeattack.member.controller.dto

import com.ssak3.timeattack.common.security.JwtTokenDto
import com.ssak3.timeattack.member.service.dto.MemberInfo

data class LoginResponse(
    val jwtTokenDto: JwtTokenDto,
    val isNewUser: Boolean,
    val memberInfo: MemberInfo,
)
