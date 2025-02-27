package com.ssak3.timeattack.task.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "여유 있게 시작 작업 생성 요청")
data class ScheduledTaskCreateRequest(
    @Schema(
        title = "작업 이름",
        description = "1이상 16자 이하",
        example = "scheduled task",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:Size(min = 1, max = 16)
    val name: String,
    @Schema(
        title = "작업 마감 시간",
        description = "과거 시간을 입력할 수 없습니다.",
        type = "string",
        example = "2025-12-31 00:00:00",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:NotNull
    @field:FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val dueDatetime: LocalDateTime,
    @Schema(
        title = "작은 행동",
        description = "1이상 16자 이하",
        example = "trigger action",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:Size(min = 1, max = 16)
    val triggerAction: String,
    @Schema(title = "소요 시간(분)", description = "분 단위 시간", example = "60", requiredMode = RequiredMode.REQUIRED)
    @field:Positive
    val estimatedTime: Int,
    @Schema(
        title = "작은 행동 알림 시간",
        type = "string",
        example = "2025-12-30 20:00:00",
        requiredMode = RequiredMode.REQUIRED,
    )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @field:FutureOrPresent
    val triggerActionAlarmTime: LocalDateTime,
    @Schema(
        title = "작업 유형",
        allowableValues = ["공부", "글쓰기", "운동", "프로그래밍", "그림∙디자인", "과제"],
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:NotBlank
    val taskType: String,
    @Schema(title = "작업 분위기", allowableValues = ["긴급한", "신나는", "감성적인", "조용한"], requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val taskMode: String,
)
