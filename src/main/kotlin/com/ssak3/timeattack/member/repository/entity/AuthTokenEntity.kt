package com.ssak3.timeattack.member.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "auth_token")
class AuthTokenEntity(
    @Id
    val memberId: Long,
    var refreshToken: String,
)
