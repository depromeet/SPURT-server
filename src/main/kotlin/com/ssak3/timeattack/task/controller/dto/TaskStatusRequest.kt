package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.task.domain.TaskStatus
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED

@Schema(description = "작업 상태 변경 요청")
data class TaskStatusRequest(
    @Schema(title = "변경할 상태", example = "WARMING_UP | FOCUSED | COMPLETE", requiredMode = REQUIRED)
    val status: TaskStatus,
)
