package com.ssak3.timeattack.retrospection.service

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.retrospection.controller.dto.RetrospectionCreateRequest
import com.ssak3.timeattack.retrospection.domain.Retrospection
import com.ssak3.timeattack.retrospection.repository.entity.RetrospectionRepository
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
