package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.domain.TaskStatus
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

    /**
     * 전체 할일 조회
     */
    fun findAllTodos(id: Long): List<TaskEntity>

    /**
     * 현재 시점에서 해야 할 일 조회(=Fail 처리할 가능성 있는 작업들만 조회)
     * - 아직 마감 시간 안지났고, 현재 상태가 BEFORE, PROCRASTINATING, HOLDING_OFF, WARMING_UP인 작업들 조회
     * - 애플리케이션 완전 시작된 후 조회된 작업들을 모두 스케줄러에 등록하기 위한 쿼리
     */
    fun findTodoTasks(todoStatuses: List<TaskStatus>): List<TaskEntity>

    /**
     * 현재 활성화된(몰입중인) 작업 목록 조회
     */
    fun findActiveTasks(memberId: Long): List<TaskEntity>

    /**
     * 완료한 일 목록 조회
     * - 완료 시간 기준으로 내림차순 정렬
     */
    fun findCompletedTasksOrderByCompletedTimeDesc(memberId: Long): List<TaskEntity>

    /**
     * 미룬 일 목록 조회
     * - 마감일 기준으로 내림차순 정렬
     */
    fun findProcrastinatedTasksOrderByDueDateDesc(memberId: Long): List<TaskEntity>
}
