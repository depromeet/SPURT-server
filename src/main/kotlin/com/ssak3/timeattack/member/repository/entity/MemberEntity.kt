package com.ssak3.timeattack.member.repository.entity

import com.ssak3.timeattack.common.domain.BaseEntity
import com.ssak3.timeattack.member.domain.Member
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "member")
class MemberEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,
    @Column(name = "nickname", length = 100)
    val nickname: String,
    @Column(name = "email", length = 100)
    val email: String,
    @Column(name = "profile_image_url", length = 500)
    val profileImageUrl: String,
    @Embedded
    val oAuthProviderInfo: OAuthProviderInfo,
    @Column(name = "default_trigger_action", length = 100)
    val defaultTriggerAction: String? = null,
    val isDeleted: Boolean = false,
) : BaseEntity() {
    fun toDomain() =
        Member(
            id = id,
            nickname = nickname,
            email = email,
            profileImageUrl = profileImageUrl,
            oAuthProviderInfo = oAuthProviderInfo,
            defaultTriggerAction = defaultTriggerAction,
            isDeleted = isDeleted,
        )
}

// OAuth Provider Enum
enum class OAuthProvider {
    KAKAO,
    GOOGLE,
}
