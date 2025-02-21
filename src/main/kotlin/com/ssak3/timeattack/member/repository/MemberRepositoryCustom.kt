package com.ssak3.timeattack.member.repository

import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.member.repository.entity.OAuthProvider

interface MemberRepositoryCustom {
    fun findByProviderAndSubject(
        oauthProvider: OAuthProvider,
        subject: String,
    ): MemberEntity?

    fun findByIdOrThrow(id: Long): MemberEntity
}
