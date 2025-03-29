package com.ssak3.timeattack.retrospection.service

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.retrospection.controller.dto.RetrospectionCreateRequest
import com.ssak3.timeattack.retrospection.domain.Retrospection
import com.ssak3.timeattack.retrospection.repository.entity.RetrospectionRepository
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.service.TaskService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RetrospectionService(
    private val retrospectionRepository: RetrospectionRepository,
    private val taskService: TaskService,
) {
    @Transactional
    fun createRetrospection(
        request: RetrospectionCreateRequest,
        member: Member,
        taskId: Long,
    ) {
        val task = taskService.findTaskByIdAndMember(member, taskId)

        // 회고는 완료된 작업에 대해서만 생성할 수 있습니다.
        if (task.status != TaskStatus.COMPLETE) {
            checkNotNull(task.id, "TaskId")
            throw ApplicationException(ApplicationExceptionType.CREATE_RETROSPECTION_NOT_ALLOWED, task.id, task.status)
        }

        val retrospection =
            Retrospection(
                member = member,
                task = task,
                satisfaction = request.satisfaction,
                concentration = request.concentration,
                comment = request.comment,
            )
        retrospectionRepository.save(retrospection.toEntity())
    }
}
