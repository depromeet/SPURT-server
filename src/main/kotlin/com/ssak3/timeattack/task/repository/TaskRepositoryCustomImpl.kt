package com.ssak3.timeattack.task.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.task.domain.TaskStatus
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
                qTask.isDeleted.eq(false),
            )
            .orderBy(qTask.dueDatetime.asc())
            .fetch()
    }

    override fun findTodayTasks(memberId: Long): List<TaskEntity> {
        val todayDate = LocalDate.now()
        val nowDateTime = LocalDateTime.now()
        return queryFactory
            .select(qTask)
            .from(qTask)
            .where(
                qTask.member.id.eq(memberId)
                    .and(qTask.dueDatetime.after(nowDateTime))
                    .and(qTask.isDeleted.eq(false))
                    .and(
                        qTask.status.eq(FOCUSED)
                            .or(qTask.status.eq(WARMING_UP))
                            .or(qTask.status.eq(PROCRASTINATING))
                            .or(
                                qTask.status.eq(BEFORE)
                                    .and(
                                        qTask.triggerActionAlarmTime.between(
                                            todayDate.atStartOfDay(),
                                            todayDate.plusDays(1).atStartOfDay().minusSeconds(1),
                                        ),
                                    ),
                            ),
                    ),
            )
            .orderBy(qTask.dueDatetime.asc(), qTask.name.asc())
            .fetch()
    }

    override fun findAllTodos(id: Long): List<TaskEntity> {
        val now = LocalDateTime.now()
        return queryFactory
            .select(qTask)
            .from(qTask)
            .where(
                qTask.member.id.eq(id)
                    .and(qTask.dueDatetime.after(now))
                    .and(qTask.status.ne(TaskStatus.COMPLETE))
                    .and(qTask.isDeleted.eq(false)),
            )
            .orderBy(qTask.dueDatetime.asc(), qTask.name.asc())
            .fetch()
    }

    override fun findTasksToFail(statusesToFail: List<TaskStatus>): List<TaskEntity> {
        val now = LocalDateTime.now()

        return queryFactory
            .select(qTask)
            .from(qTask)
            .where(
                qTask.status.`in`(statusesToFail)
                    .and(qTask.dueDatetime.after(now))
                    .and(qTask.isDeleted.isFalse),
            )
            .fetch()
    }

    override fun findAbandonedOrIgnoredTasks(memberId: Long): TaskEntity? {
        val now = LocalDateTime.now()
        val threeMinutesAgo = now.minusMinutes(3)

        return queryFactory
            .select(qTask)
            .from(qTask)
            .where(
                qTask.member.id.eq(memberId),
                qTask.dueDatetime.after(now),
                qTask.status.eq(WARMING_UP)
                    .or(
                        qTask.status.eq(BEFORE)
                            .and(qTask.triggerActionAlarmTime.isNotNull)
                            .and(qTask.triggerActionAlarmTime.before(threeMinutesAgo)),
                    ),
                qTask.isDeleted.eq(false),
            )
            .orderBy(qTask.triggerActionAlarmTime.desc(), qTask.dueDatetime.asc())
            .fetchFirst()
    }
}
