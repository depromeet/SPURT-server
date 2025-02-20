package com.ssak3.timeattack.member.infrastructure

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider

interface MemberCustomRepository {
    fun findByProviderAndSubject(oauthProvider: OAuthProvider, subject: String): Member?

    fun findByIdOrThrow(id: Long): Member
}
