package com.ssak3.timeattack.common.security.refreshtoken

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 7)
data class RefreshTokenEntity(
    @Id
    val id: String,
    val refreshToken: String,
) {
    fun validateRefreshToken(refreshToken: String): Boolean {
        return this.refreshToken == refreshToken
    }
}
