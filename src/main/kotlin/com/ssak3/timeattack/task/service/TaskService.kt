package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.retrospection.domain.Retrospection
import com.ssak3.timeattack.retrospection.repository.RetrospectionRepository
import com.ssak3.timeattack.task.controller.dto.ScheduledTaskCreateRequest
import com.ssak3.timeattack.task.controller.dto.TaskHoldOffRequest
import com.ssak3.timeattack.task.controller.dto.TaskUpdateRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.domain.TaskStatus.COMPLETE
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.service.events.DeleteTaskNotificationEvent
import com.ssak3.timeattack.task.service.events.ReminderSaveEvent
import com.ssak3.timeattack.task.service.events.SupportAlarm
import com.ssak3.timeattack.task.service.events.SupportNotificationSaveEvent
import com.ssak3.timeattack.task.service.events.TriggerActionNotificationSaveEvent
import com.ssak3.timeattack.task.service.events.TriggerActionNotificationUpdateEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskTypeRepository: TaskTypeRepository,
    private val taskModeRepository: TaskModeRepository,
    private val personaRepository: PersonaRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val retrospectionRepository: RetrospectionRepository,
) : Logger {
    @Transactional
    fun createUrgentTask(
        member: Member,
        urgentTaskRequest: UrgentTaskRequest,
    ): Task {
        // 1. Persona 가져오기
        val persona = findPersonaByTaskTypeAndTaskMode(urgentTaskRequest.taskType, urgentTaskRequest.taskMode)

        // 2. Task 생성
        val task =
            Task(
                name = urgentTaskRequest.name,
                category = TaskCategory.URGENT,
                dueDatetime = urgentTaskRequest.dueDatetime,
                status = TaskStatus.FOCUSED,
                member = member,
                persona = persona,
            )
        // 3. Task 저장
        val savedTaskEntity = taskRepository.save(task.toEntity())

        val savedTask = Task.fromEntity(savedTaskEntity)

        // 종료 시간에 실패 체크 스케줄러 등록
        eventPublisher.publishEvent(savedTask)

        // 4. Task 반환
        return Task.fromEntity(savedTaskEntity)
    }

    @Transactional
    fun createScheduledTask(
        member: Member,
        scheduledTaskRequest: ScheduledTaskCreateRequest,
    ): Task {
        // 1. Persona 가져오기
        val persona = findPersonaByTaskTypeAndTaskMode(scheduledTaskRequest.taskType, scheduledTaskRequest.taskMode)

        // 2. Task 생성
        val task =
            Task(
                name = scheduledTaskRequest.name,
                category = TaskCategory.SCHEDULED,
                dueDatetime = scheduledTaskRequest.dueDatetime,
                status = TaskStatus.BEFORE,
                triggerAction = scheduledTaskRequest.triggerAction,
                estimatedTime = scheduledTaskRequest.estimatedTime,
                triggerActionAlarmTime = scheduledTaskRequest.triggerActionAlarmTime,
                member = member,
                persona = persona,
            )

        // 3. Task 저장
        val savedTaskEntity = taskRepository.save(task.toEntity())

        // 4. Task 이벤트 발행
        val alarmTimes = getAlarmTimes(scheduledTaskRequest.triggerActionAlarmTime)
        val triggerActionNotificationSaveEvent =
            TriggerActionNotificationSaveEvent(
                checkNotNull(member.id),
                checkNotNull(savedTaskEntity.id),
                alarmTimes,
            )
        eventPublisher.publishEvent(triggerActionNotificationSaveEvent)

        val savedTask = Task.fromEntity(savedTaskEntity)

        // 종료 시간에 실패 체크 스케줄러 등록
        eventPublisher.publishEvent(savedTask)

        // 5. Task 반환
        return savedTask
    }

    // 2분단위로 첫번째 알림 + 5번 추가 알림 = 6번
    private fun getAlarmTimes(startTime: LocalDateTime): List<LocalDateTime> =
        generateSequence(startTime) { it.plusMinutes(2) }.take(6).map { it.withSecond(0).withNano(0) }.toList()

    private fun findPersonaByTaskTypeAndTaskMode(
        taskType: String,
        taskMode: String,
    ): Persona {
        // 작업 유형 키워드 존재 검증
        val taskTypeEntity = (
            taskTypeRepository.findByName(taskType)
                ?: throw ApplicationException(
                    ApplicationExceptionType.TASK_TYPE_NOT_FOUND_BY_NAME,
                    taskType,
                )
        )

        // 작업 분위기 키워드 존재 검증
        val taskModeEntity = (
            taskModeRepository.findByName(taskMode)
                ?: throw ApplicationException(
                    ApplicationExceptionType.TASK_MODE_NOT_FOUND_BY_NAME,
                    taskMode,
                )
        )

        return Persona.fromEntity(
            personaRepository.findByTaskTypeAndTaskMode(taskTypeEntity, taskModeEntity)
                ?: throw ApplicationException(
                    ApplicationExceptionType.PERSONA_NOT_FOUND_BY_TASK_KEYWORD_COMBINATION,
                    taskType,
                    taskMode,
                ),
        )
    }

    @Transactional
    fun changeTaskStatus(
        taskId: Long,
        memberId: Long,
        newStatus: TaskStatus,
    ): Task {
        // Task 가져오기
        val task =
            Task.fromEntity(
                taskRepository.findById(taskId)
                    .orElseThrow {
                        ApplicationException(
                            ApplicationExceptionType.TASK_NOT_FOUND_BY_ID,
                            taskId.toString(),
                        )
                    },
            )

        // Task 수정 가능 여부 확인
        task.assertOwnedBy(memberId)

        // Task 상태 변경
        task.changeStatus(newStatus)

        // Task 수정 반영
        val savedTaskEntity = taskRepository.save(task.toEntity())

        logger.info("Task 상태 변경 반영: $newStatus -> ${savedTaskEntity.status}")
        return Task.fromEntity(savedTaskEntity)
    }

    fun findTodayTodoTasks(member: Member): List<Task> {
        return taskRepository.findTodayTasks(checkNotNull(member.id)).map { Task.fromEntity(it) }
    }

    fun getTaskById(id: Long): Task =
        taskRepository.findByIdOrNull(id)?.let {
            Task.fromEntity(it)
        } ?: throw ApplicationException(ApplicationExceptionType.TASK_NOT_FOUND_BY_ID, id)

    fun findTaskByIdAndMember(
        member: Member,
        taskId: Long,
    ): Task {
        checkNotNull(member.id)
        val task = findTaskById(taskId)
        task.assertOwnedBy(member.id)
        return task
    }

    fun findTaskById(taskId: Long): Task {
        val task =
            taskRepository.findByIdAndIsDeletedIsFalse(taskId)
                ?: throw ApplicationException(
                    ApplicationExceptionType.TASK_NOT_FOUND_BY_ID,
                    taskId,
                )
        return Task.fromEntity(task)
    }

    @Transactional(readOnly = true)
    fun getTodoTasksForRestOfCurrentWeek(member: Member): List<Task> {
        val today = LocalDate.now()
        // 오늘이 일요일이면 이번 주 할 일은 없다고 판단(이번주의 끝이 일요일이기 때문)
        if (today.dayOfWeek == DayOfWeek.SUNDAY) return emptyList()

        // 이번 주 = 내일(00:00:00) ~ 일요일(23:59:59)
        val tomorrow = today.plusDays(1).atStartOfDay()
        val thisSunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(23, 59, 59)

        checkNotNull(member.id) { "Member id must not be null" }
        return taskRepository.getTasksBetweenDates(member.id, tomorrow, thisSunday).map { Task.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getAbandonedOrIgnoredTasks(member: Member): Task? {
        checkNotNull(member.id) { "Member id must not be null" }
        return taskRepository.findAbandonedOrIgnoredTasks(member.id)?.let { Task.fromEntity(it) }
    }

    fun findAllTodoTasks(member: Member): List<Task> {
        return taskRepository.findAllTodos(checkNotNull(member.id)).map { Task.fromEntity(it) }
    }

    @Transactional
    fun removeTask(
        member: Member,
        taskId: Long,
    ) {
        checkNotNull(member.id)
        val task = findTaskById(taskId)
        task.assertOwnedBy(member.id)
        task.delete()
        taskRepository.save(task.toEntity())

        // Task 삭제 이벤트 발행
        eventPublisher.publishEvent(DeleteTaskNotificationEvent(member.id, checkNotNull(task.id)))
        // TODO: 이벤트 처리 실패시 어떻게 처리할지 고민
    }

    @Transactional
    fun holdOffTask(
        taskId: Long,
        member: Member,
        taskHoldOffRequest: TaskHoldOffRequest,
    ) {
        checkNotNull(member.id)
        // 1. Task 상태 변경
        val task = changeTaskStatus(taskId, member.id, TaskStatus.HOLDING_OFF)

        // 2. 리마인더 알림 시간 계산
        // 횟수만큼 반복하면서 기준 시간에서 remindInterval을 더한 시간을 계산
        checkNotNull(task.triggerActionAlarmTime)
        val reminderAlarms =
            (1..taskHoldOffRequest.remindCount).map { order ->
                val nextReminderAlarmTime =
                    taskHoldOffRequest.remindBaseTime.plusMinutes(
                        taskHoldOffRequest.remindInterval * order.toLong(),
                    )
                task.validateReminderAlarmTime(nextReminderAlarmTime)
                nextReminderAlarmTime
            }

        // 3. 리마인더 알림 저장 이벤트 발행
        eventPublisher.publishEvent(ReminderSaveEvent(member.id, taskId, reminderAlarms))
    }

    @Transactional
    fun updateTask(
        member: Member,
        taskId: Long,
        request: TaskUpdateRequest,
    ): Task {
        val task = findTaskByIdAndMember(member, taskId)
        request.name?.let { task.modifyName(it) }
        request.triggerAction?.let { task.modifyTriggerAction(it) }

        if (request.isEstimatedTimeUpdateRequest()) {
            checkNotNull(request.estimatedTime, "estimatedTime")
            checkNotNull(request.triggerActionAlarmTime, "triggerActionAlarmTime")

            task.modifyEstimatedTime(request.estimatedTime, request.triggerActionAlarmTime)
        } else if (request.isDueDatetimeUpdateRequest()) {
            val updatedDueDatetime = checkNotNull(request.dueDatetime, "dueDatetime")

            // 마감시간을 줄였는데 남은 시간이 예상 소요 시간보다 짧은 경우 즉시 몰입 시작으로 처리
            if (request.isUrgent) {
                // 즉시 몰입 시작으로 처리하기 위해 마감시간을 변경하고 status를 FOCUSED로 변경
                task.modifyToUrgentDueDatetime(updatedDueDatetime)
            } else {
                // 즉시 몰입 시작이 아닌 경우 작은행동 알림 검증을 통해 마감시간 업데이트
                checkNotNull(request.triggerActionAlarmTime, "triggerActionAlarmTime")
                task.modifyDueDatetime(updatedDueDatetime, request.triggerActionAlarmTime)
            }
        }

        // 작업 수정 및 저장
        val updatedTaskEntity = taskRepository.save(task.toEntity())

        // 작업 수정 관련 이벤트 발행
        checkNotNull(member.id, "memberId")
        publishEventForUpdateTask(member.id, taskId, request)

        return Task.fromEntity(updatedTaskEntity)
    }

    fun getActiveTasks(member: Member): List<Task> {
        checkNotNull(member.id, "memberId")
        return taskRepository.findActiveTasks(member.id).map { Task.fromEntity(it) }
    }

    private fun publishEventForUpdateTask(
        memberId: Long,
        taskId: Long,
        request: TaskUpdateRequest,
    ) {
        // 즉시 시작하게 되면 기존 알림을 삭제
        if (request.isUrgent) {
            eventPublisher.publishEvent(DeleteTaskNotificationEvent(memberId, taskId))
        }

        // 작은 행동 알림이 업데이트 되면 새로운 작은 행동 알림 업데이트 이벤트 발행
        if (request.isTriggerActionAlarmTimeUpdateRequest()) {
            val triggerActionNotificationUpdateEvent =
                TriggerActionNotificationUpdateEvent(
                    memberId,
                    taskId,
                    checkNotNull(request.triggerActionAlarmTime, "triggerActionAlarmTime"),
                )
            eventPublisher.publishEvent(triggerActionNotificationUpdateEvent)
        }
    }

    /**
     * [푸시 알림 문구 정책(index)]
     * - 초단기 할 일 (1시간 이하)
     *   1. 10분 전(5)
     * - 단기 할 일 (1~4시간 이하)
     *   1. 1시간 전(4)
     * - 중장기 할 일 (4~24시간 이하)
     *   1. 중간 지점(2)
     *   2. 1시간 전(4)
     * - 장기 할 일 (1일 이상)
     *   1. 매일 오전 9시(2)
     *   2. 마감 24시간 전(3)
     *   3. 1시간 전(4)
     */
    @Transactional
    fun requestSupportNotifications(
        taskId: Long,
        memberId: Long,
    ) {
        val task =
            taskRepository.findByIdOrNull(taskId)
                ?: throw ApplicationException(ApplicationExceptionType.TASK_NOT_FOUND_BY_ID, taskId)

        val dueDatetime = task.dueDatetime
        val interval = task.estimatedTime?.toLong() ?: Duration.between(dueDatetime, task.createdAt).toMinutes()
        logger.info("======= task($taskId) interval time is $interval")

        val supportAlarms = mutableListOf<SupportAlarm>()

        if (interval <= 60) {
            supportAlarms.add(
                SupportAlarm(
                    index = 5,
                    alarmTime = dueDatetime.minusMinutes(10),
                ),
            )
        } else if (interval <= 240) {
            supportAlarms.add(
                SupportAlarm(
                    index = 4,
                    alarmTime = dueDatetime.minusHours(1),
                ),
            )
        } else if (interval <= 1440) {
            supportAlarms.addAll(
                listOf(
                    SupportAlarm(
                        index = 2,
                        alarmTime = dueDatetime.minusMinutes(interval / 2),
                    ),
                    SupportAlarm(
                        index = 4,
                        alarmTime = dueDatetime.minusHours(1),
                    ),
                ),
            )
        } else {
            val dateTimes = getBetweenDateTimes(dueDatetime)
            dateTimes.forEach { dateTime ->
                supportAlarms.add(
                    SupportAlarm(
                        index = 1,
                        alarmTime = dateTime,
                    ),
                )
            }
            supportAlarms.addAll(
                listOf(
                    SupportAlarm(
                        index = 3,
                        alarmTime = dueDatetime.minusDays(1),
                    ),
                    SupportAlarm(
                        index = 3,
                        alarmTime = dueDatetime.minusDays(1),
                    ),
                    SupportAlarm(
                        index = 4,
                        alarmTime = dueDatetime.minusHours(1),
                    ),
                ),
            )
        }

        eventPublisher.publishEvent(
            SupportNotificationSaveEvent(
                memberId = memberId,
                taskId = taskId,
                alarmTimes = supportAlarms,
            ),
        )
    }

    private fun getBetweenDateTimes(dueDatetime: LocalDateTime): List<LocalDateTime> {
        var current = LocalDateTime.now()
        val dateTimes = mutableListOf<LocalDateTime>()

        // 오늘 이미 오전 9시가 지났으면 오늘은 알림 대상에 포함하지 않습니다.(내일부터 시작)
        if (current.hour >= 9) {
            current = current.plusDays(1)
        }

        while (current < dueDatetime) {
            dateTimes.add(current.toLocalDate().atTime(9, 0))
            current = current.plusDays(1)
        }

        return dateTimes
    }

    // 몰입이 완료되면 등록된 푸시알림 비활성화
    @Transactional
    fun inactiveSupportNotifications(
        taskId: Long,
        memberId: Long,
    ) {
        eventPublisher.publishEvent(DeleteTaskNotificationEvent(memberId = memberId, taskId = taskId))
    }

    /**
     * 작업과 관련된 회고 데이터를 함께 조회
     */
    fun getTaskWithRetrospection(
        member: Member,
        taskId: Long,
    ): Pair<Task, Retrospection?> {
        // 작업 조회 및 소유권 검증
        val task = findTaskByIdAndMember(member, taskId)
        logger.info("Task 조회: ID=${task.id}, name=${task.name}")

        // 회고 데이터 조회 (없으면 null)
        val retrospection = findRetrospectionForTask(task)
        return Pair(task, retrospection)
    }

    /**
     * 작업에 관련된 회고 데이터 조회
     * 완료 상태의 작업만 회고 데이터가 조회됨
     */
    private fun findRetrospectionForTask(task: Task): Retrospection? {
        if (task.status != COMPLETE) return null

        checkNotNull(task.id, "TaskId")
        return retrospectionRepository.findByTaskId(task.id)?.let { entity ->
            val retrospection = Retrospection.fromEntity(entity)
            logger.info("회고 조회: ID=${retrospection.id}, 만족도=${retrospection.satisfaction}")
            retrospection
        }
    }
}
