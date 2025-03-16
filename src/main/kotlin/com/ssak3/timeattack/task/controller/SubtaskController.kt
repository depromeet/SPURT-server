package com.ssak3.timeattack.task.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.common.dto.MessageResponse
import com.ssak3.timeattack.task.controller.dto.SubTaskResponse
import com.ssak3.timeattack.task.controller.dto.SubtaskCreateRequest
import com.ssak3.timeattack.task.domain.Subtask
import com.ssak3.timeattack.task.service.SubtaskService
import com.ssak3.timeattack.task.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/subtasks")
class SubtaskController(
    private val subtaskService: SubtaskService,
    private val taskService: TaskService,
) {
    @Operation(summary = "세부목표 생성/수정", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PostMapping
    fun upsert(
        @RequestBody @Valid request: SubtaskCreateRequest,
    ): ResponseEntity<SubTaskResponse> {
        val task = taskService.getTaskById(request.taskId)
        val subtask =
            Subtask(
                id = request.id,
                task = task,
                name = request.name,
            )
        val upsertedSubtask = subtaskService.upsert(subtask)
        return ResponseEntity.ok(SubTaskResponse.fromSubtask(upsertedSubtask))
    }

    @Operation(summary = "세부작업 삭제", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @DeleteMapping("/{id}")
    fun remove(
        @PathVariable(required = true) @Positive id: Long,
    ): ResponseEntity<MessageResponse> {
        subtaskService.delete(id)
        return ResponseEntity.ok(MessageResponse("Subtask removed successfully"))
    }

    @Operation(summary = "세부작업 완료 상태 업데이트", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable(required = true) @Positive id: Long,
    ): ResponseEntity<SubTaskResponse> {
        val updatedSubtask = subtaskService.updateStatus(id)
        return ResponseEntity.ok(SubTaskResponse.fromSubtask(updatedSubtask))
    }
}
