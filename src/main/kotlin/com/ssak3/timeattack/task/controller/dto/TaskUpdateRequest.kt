package com.ssak3.timeattack.task.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "작업 수정 요청")
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
    val dueDatetime: LocalDateTime? = null,
    @Schema(
        title = "지금 바로 작업 몰입을 시작할지 여부",
        description = "마감시간이 줄었는데 마감까지 시간이 예상소요시간보다 적을 때 true",
        allowableValues = ["true", "false"],
        requiredMode = RequiredMode.REQUIRED,
    )
    val isUrgent: Boolean,
) {
    @JsonIgnore
    fun isEstimatedTimeUpdateRequest(): Boolean = estimatedTime != null

    @JsonIgnore
    fun isDueDatetimeUpdateRequest(): Boolean = dueDatetime != null

    @JsonIgnore
    fun isTriggerActionAlarmTimeUpdateRequest(): Boolean = triggerActionAlarmTime != null

    @JsonIgnore
    fun validateRequest() {
        validateUpdateValueNotExists()
        validateUrgentRequest()
    }

    @JsonIgnore
    fun validateUpdateValueNotExists() {
        if (name == null &&
            triggerAction == null &&
            triggerActionAlarmTime == null &&
            estimatedTime == null &&
            dueDatetime == null
        ) {
            throw ApplicationException(ApplicationExceptionType.INVALID_UPDATE_VALUE, "수정할 값이 없습니다.")
        }
    }

    @JsonIgnore
    fun validateUrgentRequest() {
        if (isUrgent && triggerActionAlarmTime != null) {
            throw ApplicationException(
                ApplicationExceptionType.INVALID_UPDATE_VALUE,
                "isUrgent가 true 때 triggerActionAlarmTime을 업데이트 할 수 없습니다.",
            )
        }

        if (isUrgent && dueDatetime == null) {
            throw ApplicationException(
                ApplicationExceptionType.INVALID_UPDATE_VALUE,
                "isUrgent가 true일 때 dueDatetime은 필수입니다.",
            )
        }
    }
}
