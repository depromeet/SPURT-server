package com.ssak3.timeattack.task.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.task.domain.TaskStatus.BEFORE
import com.ssak3.timeattack.task.domain.TaskStatus.FOCUSED
import com.ssak3.timeattack.task.domain.TaskStatus.PROCRASTINATING
import com.ssak3.timeattack.task.domain.TaskStatus.WARMING_UP
import com.ssak3.timeattack.task.repository.entity.QTaskEntity
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import org.springframework.stereotype.Repository
import java.time.LocalDate
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

    /**
     * 오늘 해야 할 일 목록 조회
     */
    override fun findTodayTasks(
        memberId: Long,
        today: LocalDate,
    ): List<TaskEntity> {
        return queryFactory
            .select(qTask)
            .from(qTask)
            .where(qTask.member.id.eq(memberId)
                .and(
                    qTask.status.eq(FOCUSED)
                        .or(qTask.status.eq(WARMING_UP))
                        .or(qTask.status.eq(PROCRASTINATING))
                        .or(qTask.status.eq(BEFORE)
                            .and(qTask.triggerActionAlarmTime.between(
                                today.atStartOfDay(),
                                today.plusDays(1).atStartOfDay()
                            )
                        )
                )))
            .orderBy(qTask.dueDatetime.asc(), qTask.name.asc())
            .fetch()
    }
}
