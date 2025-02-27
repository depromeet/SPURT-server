package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskTypeRepository: TaskTypeRepository,
    private val taskModeRepository: TaskModeRepository,
    private val personaRepository: PersonaRepository,
) {
    @Transactional
    fun createUrgentTask(
        member: Member,
        urgentTaskRequest: UrgentTaskRequest,
    ): Task {
        // 1. 키워드 검증 진행
        val taskTypeEntity = (
            taskTypeRepository.findByName(urgentTaskRequest.taskType)
                ?: throw ApplicationException(
                    ApplicationExceptionType.TASK_TYPE_NOT_FOUND_BY_NAME,
                    urgentTaskRequest.taskType,
                )
        )
        val taskModeEntity = (
            taskModeRepository.findByName(urgentTaskRequest.taskMode)
                ?: throw ApplicationException(
                    ApplicationExceptionType.TASK_MODE_NOT_FOUND_BY_NAME,
                    urgentTaskRequest.taskMode,
                )
        )

        // 2. Persona 가져오기
        val persona =
            Persona.fromEntity(
                personaRepository.findByTaskTypeAndTaskMode(taskTypeEntity, taskModeEntity)
                    ?: throw ApplicationException(
                        ApplicationExceptionType.PERSONA_NOT_FOUND_BY_TASK_KEYWORD_COMBINATION,
                        taskTypeEntity.name,
                        taskModeEntity.name,
                    ),
            )

        // 3. Task 생성
        val task =
            Task(
                name = urgentTaskRequest.name,
                category = TaskCategory.URGENT,
                dueDatetime = urgentTaskRequest.dueDatetime,
                status = TaskStatus.BEFORE,
                member = member,
                persona = persona,
            )
        // 4. Task 저장
        val savedTaskEntity = taskRepository.save(task.toEntity())

        // 5. Task 반환
        return Task.fromEntity(savedTaskEntity)
    }
}
