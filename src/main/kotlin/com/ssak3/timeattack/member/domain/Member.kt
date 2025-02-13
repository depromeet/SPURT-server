package com.ssak3.timeattack.member.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "member")
class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "nickname", length = 100)
    var nickname: String,

    @Column(name = "email", length = 100)
    var email: String,

    @Column(name = "profile_image_url", length = 500)
    var profileImageUrl: String,

    @Column(name = "oauth_provider", length = 50)
    @Enumerated(EnumType.STRING)
    val oauthProvider: OAuthProvider,

    @Column(name = "sub", length = 50)
    val sub: String,

    @Column(name = "default_trigger_action", length = 100)
    var defaultTriggerAction: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_deleted")
    var isDeleted: Boolean = false
) {

    // jpa에서는 기본 생성자가 필수
    constructor() : this(
        id = null,
        nickname = "",
        email = "",
        profileImageUrl = "",
        oauthProvider = OAuthProvider.KAKAO,
        sub = ""
    )

    // 업데이트 시간을 자동으로 갱신하기 위한 메서드
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}

// OAuth Provider Enum
enum class OAuthProvider {
    KAKAO,
    GOOGLE,
}