package com.ssak3.timeattack.persona.client

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google.cloud")
data class GoogleCloudProperties(
    val apiKey: String,
)
