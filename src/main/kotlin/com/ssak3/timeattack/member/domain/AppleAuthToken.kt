package com.ssak3.timeattack.member.domain

import com.ssak3.timeattack.member.repository.entity.AppleAuthTokenEntity

class AppleAuthToken(
    val memberId: Long,
    var refreshToken: String,
) {
    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    fun toEntity() =
        AppleAuthTokenEntity(
            memberId = memberId,
            refreshToken = refreshToken,
        )

    companion object {
        fun fromEntity(appleAuthTokenEntity: AppleAuthTokenEntity) =
            AppleAuthToken(
                memberId = appleAuthTokenEntity.memberId,
                refreshToken = appleAuthTokenEntity.refreshToken,
            )
    }
}
