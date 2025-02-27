package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.task.controller.dto.ScheduledTaskCreateRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.service.events.ScheduledTaskSaveEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskTypeRepository: TaskTypeRepository,
    private val taskModeRepository: TaskModeRepository,
    private val personaRepository: PersonaRepository,

    private val eventPublisher: ApplicationEventPublisher,
) {
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
    fun createScheduledTask(member: Member, scheduledTaskRequest: ScheduledTaskCreateRequest): Task {
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
                member = member,
                persona = persona,
            )

        task.validateTriggerActionAlarmTime(scheduledTaskRequest.triggerActionAlarmTime)

        // 3. Task 저장
        val savedTaskEntity = taskRepository.save(task.toEntity())

        // 4. Task 이벤트 발행
        // savedTaskEntity id 값이 null이 될 수 없으므로 !! 연산자 사용
        val scheduledTaskSaveEvent = ScheduledTaskSaveEvent(savedTaskEntity.id!!, savedTaskEntity.category, scheduledTaskRequest.triggerActionAlarmTime)
        eventPublisher.publishEvent(scheduledTaskSaveEvent)

        // 5. Task 반환
        return Task.fromEntity(savedTaskEntity)

    }

    fun findPersonaByTaskTypeAndTaskMode(taskType: String, taskMode: String): Persona {
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
}
