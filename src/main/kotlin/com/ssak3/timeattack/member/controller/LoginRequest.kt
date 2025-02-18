package com.ssak3.timeattack.member.controller

import com.ssak3.timeattack.member.domain.OAuthProvider

data class LoginRequest(
    val authCode: String,
    val provider: OAuthProvider,
    // TODO: 기기 정보, 기기 타입(ANDROID, IOS)
)