package com.ssak3.timeattack.retrospection.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.retrospection.repository.entity.QRetrospectionEntity
import com.ssak3.timeattack.retrospection.repository.entity.RetrospectionEntity
import com.ssak3.timeattack.task.repository.entity.QTaskEntity
import org.springframework.stereotype.Repository

@Repository
class RetrospectionRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : RetrospectionRepositoryCustom {
    private val qRetrospectionEntity = QRetrospectionEntity.retrospectionEntity
    private val qTaskEntity = QTaskEntity.taskEntity

    override fun findAllByMemberId(memberId: Long): List<RetrospectionEntity> {
        return queryFactory
            .selectFrom(qRetrospectionEntity)
            .join(qTaskEntity).on(qRetrospectionEntity.task.id.eq(qTaskEntity.id).and(qTaskEntity.isDeleted.isFalse))
            .where(qRetrospectionEntity.member.id.eq(memberId))
            .fetch()
    }
}
