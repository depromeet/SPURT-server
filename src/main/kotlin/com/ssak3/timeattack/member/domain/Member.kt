package com.ssak3.timeattack.member.domain

import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo
import java.time.LocalDateTime

class Member(
    val id: Long? = null,
    var nickname: String,
    val email: String,
    val profileImageUrl: String?,
    val oAuthProviderInfo: OAuthProviderInfo,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    var isDeleted: Boolean = false,
) {
    fun delete() {
        this.isDeleted = true
    }

    fun toEntity() =
        MemberEntity(
            id = id,
            nickname = nickname,
            email = email,
            profileImageUrl = profileImageUrl,
            oAuthProviderInfo = oAuthProviderInfo,
            isDeleted = isDeleted,
        )

    override fun toString(): String {
        return "Member(id=$id, nickname='$nickname', email='$email', " +
            "AuthProviderInfo=$oAuthProviderInfo, createdAt=$createdAt)"
    }

    companion object {
        fun fromEntity(memberEntity: MemberEntity) =
            Member(
                id = memberEntity.id,
                nickname = memberEntity.nickname,
                email = memberEntity.email,
                profileImageUrl = memberEntity.profileImageUrl,
                oAuthProviderInfo = memberEntity.oAuthProviderInfo,
                createdAt = memberEntity.createdAt,
                updatedAt = memberEntity.updatedAt,
                isDeleted = memberEntity.isDeleted,
            )
    }
}
