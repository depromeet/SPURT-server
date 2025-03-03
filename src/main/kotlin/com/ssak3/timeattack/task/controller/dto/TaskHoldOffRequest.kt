package com.ssak3.timeattack.task.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive

@Schema(description = "리마인더 알림 저장 요청")
data class TaskHoldOffRequest(
    @Schema(
        title = "리마인드 알림 간격",
        description = "분 단위 시간",
        example = "15",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @field:Positive
    val remindTerm: Int,
)
