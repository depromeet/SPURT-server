package com.ssak3.timeattack.member.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.member.repository.entity.OAuthProvider
import com.ssak3.timeattack.member.repository.entity.QMemberEntity
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : MemberRepositoryCustom {
    override fun findByProviderAndSubject(
        oauthProvider: OAuthProvider,
        subject: String,
    ): MemberEntity? {
        val qMemberEntity = QMemberEntity.memberEntity
        return queryFactory
            .select(qMemberEntity)
            .from(qMemberEntity)
            .where(
                qMemberEntity.oAuthProviderInfo.oauthProvider.eq(oauthProvider),
                qMemberEntity.oAuthProviderInfo.subject.eq(subject),
            )
            .fetchOne()
    }
}
