package com.ssak3.timeattack.member.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kakao")
data class KakaoProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)