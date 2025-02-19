package com.ssak3.timeattack.member.infrastructure

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider

class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : MemberRepositoryCustom {

    override fun findByProviderAndSubject(oauthProvider: OAuthProvider, subject: String): Member? {
        return queryFactory
            .select()

    }
}