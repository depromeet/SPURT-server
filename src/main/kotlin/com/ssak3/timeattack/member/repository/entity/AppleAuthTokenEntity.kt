package com.ssak3.timeattack.member.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "apple_auth_token")
class AppleAuthTokenEntity(
    @Id
    val memberId: Long,
    var refreshToken: String,
)
