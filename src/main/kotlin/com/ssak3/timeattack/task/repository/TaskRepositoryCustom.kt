package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.repository.entity.TaskEntity
import java.time.LocalDateTime

interface TaskRepositoryCustom {
    /**
     * 두 날짜 사이에 해야 할 일 목록 조회
     */
    fun getTasksBetweenDates(
        memberId: Long,
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<TaskEntity>

    /**
     * 오늘 해야 할 일 목록 조회
     */
    fun findTodayTasks(memberId: Long): List<TaskEntity>

    /**
     * 이탈한 작업 중 가장 최근에 알림이 발생한 작업 하나를 조회
     * - 상태가 WARMING_UP인 작업 (사용자가 알림을 받고 앱에 들어왔다가 중간에 이탈)
     * - 상태가 BEFORE이고 작은 행동 알림 시간이 3분 이상 지난 작업 (알림을 무시한 경우)
     *
     * 정렬 기준:
     * - 가장 최근에 알림이 발생한 작업이 우선 (triggerActionAlarmTime 내림차순)
     * - 같은 알림 시간이라면 마감 시간이 가까운 작업 우선 (dueDatetime 오름차순)
     *
     * @return 조건에 맞는 작업 중 정렬 기준에 따라 첫 번째 작업, 없으면 null
     */
    fun findAbandonedOrIgnoredTasks(memberId: Long): TaskEntity?

    fun findAllTodos(id: Long): List<TaskEntity>
}
