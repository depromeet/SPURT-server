package com.ssak3.timeattack.task.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "즉시 시작 작업 생성 요청")
data class UrgentTaskRequest(
    @Schema(title = "작업 이름", description = "1이상 16자 이하", example = "urgent task", requiredMode = RequiredMode.REQUIRED)
    @field:Size(min = 1, max = 16)
    val name: String,
    @Schema(
        title = "작업 마감 시간",
        description = "과거 시간을 입력할 수 없습니다.",
        example = "25-12-31 00:00:00",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:NotNull
    @field:FutureOrPresent
    val dueDatetime: LocalDateTime,
    @Schema(title = "작업 유형", example = "프로그래밍", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val taskType: String,
    @Schema(title = "작업 분위기", example = "즐거운", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val taskMode: String,
)
