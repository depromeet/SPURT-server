package com.ssak3.timeattack.task.controller

import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskResponse
import com.ssak3.timeattack.task.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/task")
class TaskController(
    private val taskService: TaskService,
) {
    @Operation(summary = "긴급 업무 생성", security = [SecurityRequirement(name = "BearerAuth")])
    @PostMapping("/urgent")
    fun createUrgentTask(
        @RequestParam memberId: Long,
        @RequestBody @Valid urgentTaskRequest: UrgentTaskRequest,
    ): ResponseEntity<UrgentTaskResponse> {
        // TODO: 인증인가 로직 완료 후 memberId 인증 매핑 어노테이션으로 가져오도록 수정
        val createdUrgentTask = taskService.createUrgentTask(memberId, urgentTaskRequest)
        return ResponseEntity.ok(UrgentTaskResponse.from(createdUrgentTask))
    }
}
