package com.ssak3.timeattack.member.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.Base64

@ConfigurationProperties(prefix = "apple")
data class AppleProperties(
    val clientId: String,
    val redirectUri: String,
    val aud: String,
    val teamId: String,
    val keyId: String,
    val privateKey: String,
) {
    fun getDecodePrivateKey(): String {
        return String(Base64.getDecoder().decode(privateKey))
    }
}
