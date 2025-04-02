package com.ssak3.timeattack.task.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

@Schema(description = "세부목표 생성/수정 요청 ")
data class SubtaskUpsertRequest(
    @Schema(
        title = "세부목표 id",
        description = "세부목표 수정시 필요",
        example = "1",
        requiredMode = RequiredMode.NOT_REQUIRED,
    )
    val id: Long = 0,
    @Schema(
        title = "작업 id",
        description = "세부목표가 해당하는 작업의 id",
        example = "1",
        requiredMode = REQUIRED,
    )
    @field:Positive
    val taskId: Long,
    @Schema(
        title = "세부목표 이름",
        description = "1이상 40자 이하",
        example = "created/updated subtask name",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:Size(min = 1, max = 40)
    val name: String,
)
