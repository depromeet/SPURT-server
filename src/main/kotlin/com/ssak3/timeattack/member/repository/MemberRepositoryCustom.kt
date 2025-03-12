package com.ssak3.timeattack.member.repository

import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.entity.MemberEntity

interface MemberRepositoryCustom {
    fun findByProviderAndSubject(
        oauthProvider: OAuthProvider,
        subject: String,
    ): MemberEntity?

    fun findByIdOrThrow(id: Long): MemberEntity?
}
