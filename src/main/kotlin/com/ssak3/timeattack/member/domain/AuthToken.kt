package com.ssak3.timeattack.member.domain

import com.ssak3.timeattack.member.repository.entity.AuthTokenEntity

class AuthToken(
    val memberId: Long,
    var refreshToken: String,
) {
    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    fun toEntity() =
        AuthTokenEntity(
            memberId = memberId,
            refreshToken = refreshToken,
        )

    companion object {
        fun fromEntity(authTokenEntity: AuthTokenEntity) =
            AuthToken(
                memberId = authTokenEntity.memberId,
                refreshToken = authTokenEntity.refreshToken,
            )
    }
}
