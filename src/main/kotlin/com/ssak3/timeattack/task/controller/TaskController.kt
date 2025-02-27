package com.ssak3.timeattack.task.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType.UNAUTHORIZED_ACCESS
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.task.controller.dto.UpdateTaskStatusRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskResponse
import com.ssak3.timeattack.task.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/tasks")
class TaskController(
    private val taskService: TaskService,
) {
    @Operation(summary = "긴급 업무 생성", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PostMapping("/urgent")
    fun createUrgentTask(
        @AuthenticationPrincipal member: Member,
        @RequestBody @Valid urgentTaskRequest: UrgentTaskRequest,
    ): ResponseEntity<UrgentTaskResponse> {
        val createdUrgentTask = taskService.createUrgentTask(member, urgentTaskRequest)
        return ResponseEntity.ok(UrgentTaskResponse.from(createdUrgentTask))
    }

    @PatchMapping("/{taskId}/status")
    fun changeStatus(
        @PathVariable(required = true) taskId: Long,
        @AuthenticationPrincipal member: Member,
        @RequestBody @Valid updateTaskStatusRequest: UpdateTaskStatusRequest,
    ) {
        checkNotNull(member.id) { throw ApplicationException(UNAUTHORIZED_ACCESS) }
        taskService.changeTaskStatus(taskId, member.id, updateTaskStatusRequest)
    }
}
