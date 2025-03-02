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
    override fun findTodayTasks(memberId: Long): List<TaskEntity> {
        val todayDate = LocalDate.now()
        val nowDateTime = LocalDateTime.now()
        return queryFactory
            .select(qTask)
            .from(qTask)
            .where(
                qTask.member.id.eq(memberId)
                    .and(qTask.dueDatetime.after(nowDateTime))
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

    /**
     * 중간에 이탈한 작업 또는 작은행동 푸시 알림 무시한 작업들 중
     * - 작업을 시작하고 중간에 이탈한 작업
     * - 작은행동 푸시 알림 무시하고 3분 이상 지난 작업
     */
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
                            .and(qTask.triggerActionAlarmTime.before(threeMinutesAgo))
                            .and(qTask.triggerActionAlarmTime.isNotNull),
                    ),
            )
            // TODO: 정렬 조건 기획 정해지는대로 수정
            .orderBy(qTask.dueDatetime.asc(), qTask.name.asc())
            .fetchFirst()
    }
}
