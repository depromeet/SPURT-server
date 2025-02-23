package com.ssak3.timeattack.member.domain

import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo

data class Member(
    val id: Long? = null,
    var nickname: String,
    val email: String,
    val profileImageUrl: String,
    val oAuthProviderInfo: OAuthProviderInfo,
    val isDeleted: Boolean = false,
) {
    fun toEntity() =
        MemberEntity(
            id = id,
            nickname = nickname,
            email = email,
            profileImageUrl = profileImageUrl,
            oAuthProviderInfo = oAuthProviderInfo,
            isDeleted = isDeleted,
        )

    companion object {
        fun toDomain(memberEntity: MemberEntity) =
            Member(
                id = memberEntity.id,
                nickname = memberEntity.nickname,
                email = memberEntity.email,
                profileImageUrl = memberEntity.profileImageUrl,
                oAuthProviderInfo = memberEntity.oAuthProviderInfo,
                isDeleted = memberEntity.isDeleted,
            )
    }
}
