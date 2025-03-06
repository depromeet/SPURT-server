package com.ssak3.timeattack.task.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.ssak3.timeattack.common.validations.NotAllFieldNull
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "작업 수정 요청")
@NotAllFieldNull
data class TaskUpdateRequest(
    @Schema(
        title = "작업 이름",
        description = "1이상 16자 이하",
        example = "updated task name",
        requiredMode = RequiredMode.NOT_REQUIRED,
    )
    @field:Size(min = 1, max = 16)
    val name: String? = null,

    @Schema(
        title = "작은 행동",
        description = "1이상 16자 이하",
        example = "trigger action",
        requiredMode = RequiredMode.NOT_REQUIRED,
    )
    @field:Size(min = 1, max = 16)
    val triggerAction: String? = null,

    @Schema(
        title = "작은 행동 알림 시간",
        type = "string",
        example = "2025-12-30 20:00:00",
        requiredMode = RequiredMode.NOT_REQUIRED,
    )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @field:FutureOrPresent
    val triggerActionAlarmTime: LocalDateTime? = null,

    @Schema(title = "소요 시간(분)", description = "분 단위 시간", example = "60", requiredMode = RequiredMode.NOT_REQUIRED)
    @field:Positive
    val estimatedTime: Int? = null,

    @Schema(
        title = "작업 마감 시간",
        description = "과거 시간을 입력할 수 없습니다.",
        type = "string",
        example = "2025-12-31 00:00:00",
        requiredMode = RequiredMode.NOT_REQUIRED,
    )
    @field:FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val dueDatetime: LocalDateTime? = null
)
