package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaSpringDataRepository
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.repository.TaskModeSpringDataRepository
import com.ssak3.timeattack.task.repository.TaskSpringDataRepository
import com.ssak3.timeattack.task.repository.TaskTypeSpringDataRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskService(
    private val taskSpringDataRepository: TaskSpringDataRepository,
    private val taskTypeSpringDataRepository: TaskTypeSpringDataRepository,
    private val taskModeSpringDataRepository: TaskModeSpringDataRepository,
    private val memberRepository: MemberRepository,
    private val personaSpringDataRepository: PersonaSpringDataRepository,
) {
    @Transactional
    fun createUrgentTask(
        memberId: Long,
        urgentTaskRequest: UrgentTaskRequest,
    ): Task {
        val member =
            memberRepository.findById(memberId)
                .orElseThrow { throw ApplicationException(ApplicationExceptionType.MEMBER_NOT_FOUND_BY_ID, memberId) }
        // 1. 키워드 검증 진행
        val taskTypeEntity = (
            taskTypeSpringDataRepository.findByName(urgentTaskRequest.taskType)
                ?: throw ApplicationException(
                    ApplicationExceptionType.TASK_TYPE_NOT_FOUND_BY_NAME,
                    urgentTaskRequest.taskType,
                )
        )
        val taskModeEntity = (
            taskModeSpringDataRepository.findByName(urgentTaskRequest.taskMode)
                ?: throw ApplicationException(
                    ApplicationExceptionType.TASK_MODE_NOT_FOUND_BY_NAME,
                    urgentTaskRequest.taskMode,
                )
        )

        // 2. Persona 가져오기
        val persona = Persona.fromEntity(
                personaSpringDataRepository.findByTaskTypeEntityAndTaskModeEntity(taskTypeEntity, taskModeEntity)
                    ?: throw ApplicationException(
                        ApplicationExceptionType.PERSONA_NOT_FOUND_BY_TASK_KEYWORD_COMBINATION,
                        taskTypeEntity.name,
                        taskModeEntity.name,
                    )
            )

        // 3. Task 생성
        val task =
            Task(
                name = urgentTaskRequest.name,
                category = TaskCategory.URGENT,
                dueDatetime = urgentTaskRequest.dueDatetime,
                status = TaskStatus.BEFORE,
                // TODO: MemberEntity로 변경
                member = member,
                persona = persona,
            )
        // 4. Task 저장
        val savedTaskEntity = taskSpringDataRepository.save(task.toEntity())

        // 5. Task 반환
        return Task.fromEntity(savedTaskEntity)
    }
}
