package com.ssak3.timeattack.retrospection.service

import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.retrospection.controller.dto.RetrospectionCreateRequest
import com.ssak3.timeattack.retrospection.domain.Retrospection
import com.ssak3.timeattack.retrospection.repository.RetrospectionRepository
import com.ssak3.timeattack.task.domain.TaskStatus.COMPLETE
import com.ssak3.timeattack.task.domain.TaskStatus.FOCUSED
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
        checkNotNull(task.id, "TaskId")
        checkNotNull(member.id, "MemberId")

        // 회고는 완료 또는 집중 상태의 Task에 대해서만 생성 가능
        task.validateRetrospectionCreation()

        // 회고 푸시 알림을 통해서 들어온 경우, 현재 Task 상태는 FOCUSED -> 회고 진행 시, Task 상태를 COMPLETE로 변경
        if (task.status == FOCUSED) {
            task.changeStatus(COMPLETE)
            taskService.changeTaskStatus(task.id, member.id, COMPLETE)
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
