package com.ssak3.timeattack.member.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.member.repository.entity.QMemberEntity
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : MemberRepositoryCustom {
    private val qMemberEntity: QMemberEntity = QMemberEntity.memberEntity

    override fun findByProviderAndSubject(
        oauthProvider: OAuthProvider,
        subject: String,
    ): MemberEntity? {
        return queryFactory
            .select(qMemberEntity)
            .from(qMemberEntity)
            .where(
                qMemberEntity.oAuthProviderInfo.oauthProvider.eq(oauthProvider),
                qMemberEntity.oAuthProviderInfo.subject.eq(subject),
            )
            .fetchOne()
    }

    override fun findByIdOrThrow(id: Long): MemberEntity? {
        return queryFactory
            .select(qMemberEntity)
            .from(qMemberEntity)
            .where(qMemberEntity.id.eq(id))
            .fetchOne()
    }
}
