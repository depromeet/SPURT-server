package com.ssak3.timeattack.member.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "apple")
data class AppleProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)
