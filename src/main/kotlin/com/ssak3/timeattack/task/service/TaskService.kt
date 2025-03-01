package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.task.controller.dto.ScheduledTaskCreateRequest
import com.ssak3.timeattack.task.controller.dto.TaskStatusRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.service.events.ScheduledTaskSaveEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskTypeRepository: TaskTypeRepository,
    private val taskModeRepository: TaskModeRepository,
    private val personaRepository: PersonaRepository,
    private val eventPublisher: ApplicationEventPublisher,
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
                status = TaskStatus.BEFORE,
                member = member,
                persona = persona,
            )
        // 3. Task 저장
        val savedTaskEntity = taskRepository.save(task.toEntity())

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

        task.validateTriggerActionAlarmTime(scheduledTaskRequest.triggerActionAlarmTime)

        // 3. Task 저장
        val savedTaskEntity = taskRepository.save(task.toEntity())

        // 4. Task 이벤트 발행
        val scheduledTaskSaveEvent =
            ScheduledTaskSaveEvent(
                checkNotNull(member.id),
                checkNotNull(savedTaskEntity.id),
                scheduledTaskRequest.triggerActionAlarmTime,
            )
        eventPublisher.publishEvent(scheduledTaskSaveEvent)

        // 5. Task 반환
        return Task.fromEntity(savedTaskEntity)
    }

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
        request: TaskStatusRequest,
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
        task.assertModifiableBy(memberId)

        // Task 상태 변경
        task.changeStatus(request.status)

        // Task 수정 반영
        val savedTaskEntity = taskRepository.save(task.toEntity())

        logger.info("Task 상태 변경 반영: ${request.status} -> ${savedTaskEntity.status}")
        return Task.fromEntity(savedTaskEntity)
    }

    fun findTodayTasks(member: Member): List<Task> {
        return taskRepository.findTodayTasks(checkNotNull(member.id), LocalDate.now()).map { Task.fromEntity(it) }
    }

    fun getTaskById(id: Long): Task =
        taskRepository.findByIdOrNull(id)?.let {
            Task.fromEntity(it)
        } ?: throw IllegalArgumentException("Task not found")

    @Transactional(readOnly = true)
    fun getTasksForRestOfCurrentWeek(member: Member): List<Task> {
        val today = LocalDate.now()
        // 오늘이 일요일이면 이번 주 할 일은 없다고 판단(이번주의 끝이 일요일이기 때문)
        if (today.dayOfWeek == DayOfWeek.SUNDAY) return emptyList()

        // 이번 주 = 내일(00:00:00) ~ 일요일(23:59:59)
        val tomorrow = today.plusDays(1).atStartOfDay()
        val thisSunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(23, 59, 59)

        checkNotNull(member.id) { "Member id must not be null" }
        return taskRepository.getTasksBetweenDates(member.id, tomorrow, thisSunday).map { Task.fromEntity(it) }
    }
}
