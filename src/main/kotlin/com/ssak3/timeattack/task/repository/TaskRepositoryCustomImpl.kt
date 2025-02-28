package com.ssak3.timeattack.task.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.task.domain.TaskStatus.BEFORE
import com.ssak3.timeattack.task.repository.entity.QTaskEntity
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TaskRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : TaskRepositoryCustom {
    private val qTask: QTaskEntity = QTaskEntity.taskEntity

    /**
     * 두 날짜 사이에 해야 할 일 목록 조회
     */
    override fun getTasksBetweenDates(
        memberId: Long,
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<TaskEntity> {
        return queryFactory
            .select(qTask)
            .from(qTask)
            .where(
                qTask.member.id.eq(memberId),
                qTask.status.eq(BEFORE),
                qTask.triggerActionAlarmTime.between(start, end),
            )
            .orderBy(qTask.dueDatetime.asc())
            .fetch()
    }
}
