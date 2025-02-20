package com.ssak3.timeattack.member.infrastructure

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.domain.QMember
import org.springframework.stereotype.Repository

@Repository
class MemberCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : MemberCustomRepository {

    override fun findByProviderAndSubject(oauthProvider: OAuthProvider, subject: String): Member? {
        val qMember = QMember.member
        return queryFactory
            .select(qMember)
            .from(qMember)
            .where(
                qMember.oAuthProviderInfo.oauthProvider.eq(oauthProvider),
                qMember.oAuthProviderInfo.subject.eq(subject)
            )
            .fetchOne()
    }

    override fun findByIdOrThrow(id: Long): Member {
        val qMember = QMember.member
        return queryFactory
            .select(qMember)
            .from(qMember)
            .where(qMember.id.eq(id))
            .fetchOne()
            ?: throw ApplicationException(ApplicationExceptionType.MEMBER_NOT_FOUND_BY_ID)
    }
}
