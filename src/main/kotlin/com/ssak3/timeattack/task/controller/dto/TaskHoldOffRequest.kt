package com.ssak3.timeattack.task.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.ssak3.timeattack.task.controller.dto.validations.AllowedIntValues
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

@Schema(description = "리마인더 알림 저장 요청")
data class TaskHoldOffRequest(
    @Schema(
        title = "리마인드 알림 간격",
        description = "분 단위 시간",
        allowableValues = ["15", "30", "60"],
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @field:AllowedIntValues(values = [15, 30, 60])
    val remindTerm: Int,

    @Schema(
        title = "리마인드 횟수",
        description = "리마인드 횟수",
        allowableValues = ["1", "2", "3"],
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @field:AllowedIntValues(values = [1, 2, 3])
    val remindCount: Int,

    @Schema(
        title = "알림 생성 기준 시간",
        description = "이 시간을 기준으로 remindTerm 씩 더해진 시간에 알림이 생성됩니다.",
        example = "2025-12-30 20:00:00",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val remindBaseTime: LocalDateTime,

)
