package com.ssak3.timeattack.member.service.dto

import com.ssak3.timeattack.common.security.JwtTokenDto

/**
 * AuthService의 authenticateAndRegister() 메서드의 반환값
 */
data class LoginResult(
    val jwtTokenDto: JwtTokenDto,
    val isNewUser: Boolean,
    val memberInfo: MemberInfo,
)

data class MemberInfo(
    val memberId: Long,
    val nickname: String,
    val email: String,
    val profileImageUrl: String?,
)
