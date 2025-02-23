package com.ssak3.timeattack.external.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.util.Base64

@Configuration
class FirebaseConfig(
    private val fcmProperties: FcmProperties,
) {
    init {
        if (FirebaseApp.getApps().isEmpty()) {
            val secretKey = getKey()
            val options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(secretKey)).build()

            FirebaseApp.initializeApp(options)
        }
    }

    private fun getKey(): ByteArrayInputStream {
        val encodedSecretKey = fcmProperties.secretKey
        val decodedKey = Base64.getDecoder().decode(encodedSecretKey)
        return ByteArrayInputStream(decodedKey)
    }
}

@ConfigurationProperties(prefix = "fcm")
data class FcmProperties(
    val secretKey: String,
)
