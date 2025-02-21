package com.ssak3.timeattack.member.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.member.domain.QMember
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.member.repository.entity.OAuthProvider
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : MemberRepositoryCustom {
    override fun findByProviderAndSubject(
        oauthProvider: OAuthProvider,
        subject: String,
    ): MemberEntity? {
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
