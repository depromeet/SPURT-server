package com.ssak3.timeattack.task.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.task.repository.entity.QTaskEntity
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class TaskRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : TaskRepositoryCustom {
    private val qTask: QTaskEntity = QTaskEntity.taskEntity

    /**
     * 날짜 범위 내의 할 일들 중 가장 빨리 마감되는 두 가지 할 일을 가져옵니다.
     */
    override fun getNextTwoTasksThisWeek(
        memberId: Long,
        start: LocalDate,
        end: LocalDate,
    ): List<TaskEntity> {
        return queryFactory
            .select(qTask)
            .from(qTask)
            .where(
                qTask.member.id.eq(memberId),
                qTask.dueDatetime.between(start.atStartOfDay(), end.atTime(23, 59, 59)),
            )
            .orderBy(qTask.dueDatetime.asc())
            .limit(2)
            .fetch()
    }
}
