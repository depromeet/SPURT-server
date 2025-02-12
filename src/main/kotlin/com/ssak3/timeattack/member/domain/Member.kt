package com.ssak3.timeattack.member.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val memberId: Long? = null,

    @Column(name = "nickname", length = 100, nullable = false)
    var nickname: String,

    @Column(name = "email", length = 100, nullable = false)
    var email: String,

    @Column(name = "profile_image_url", length = 500, nullable = false)
    var profileImageUrl: String,

    @Column(name = "oauth_provider", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    val oauthProvider: OAuthProvider,

    @Column(name = "sub", length = 50, nullable = false)
    val sub: String,

    @Column(name = "default_trigger_action", length = 100)
    var defaultTriggerAction: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "isDeleted", nullable = false)
    var isDeleted: Boolean = false
) {

    // jpa에서는 기본 생성자가 필수
    constructor() : this(
        memberId = 0,
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