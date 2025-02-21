package com.ssak3.timeattack.member.domain

import com.ssak3.timeattack.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

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
    @Embedded
    val oAuthProviderInfo: OAuthProviderInfo,
    @Column(name = "default_trigger_action", length = 100)
    var defaultTriggerAction: String? = null,
) : BaseEntity()

// OAuth Provider Enum
enum class OAuthProvider {
    KAKAO,
    GOOGLE,
}
