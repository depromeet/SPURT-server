package com.ssak3.timeattack.member.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.domain.QMember
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : MemberRepositoryCustom {
    override fun findByProviderAndSubject(
        oauthProvider: OAuthProvider,
        subject: String,
    ): Member? {
        val qMember = QMember.member
        return queryFactory
            .select(qMember)
            .from(qMember)
            .where(
                qMember.oAuthProviderInfo.oauthProvider.eq(oauthProvider),
                qMember.oAuthProviderInfo.subject.eq(subject),
            )
            .fetchOne()
    }
}
