package com.ssak3.timeattack.member.controller.dto

import com.ssak3.timeattack.common.domain.DevicePlatform
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.NotBlank

@Schema(description = "애플 로그인 요청")
data class AppleLoginRequest(
    @Schema(title = "인가 코드", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val authCode: String,
    @Schema(
        title = "이름",
        example = "조익현",
        description = "최초 로그인 시 user 정보 주는 경우에만 포함",
        requiredMode = RequiredMode.NOT_REQUIRED,
    )
    val nickname: String?,
    @Schema(
        title = "이메일",
        example = "andantej99@naver.com",
        description = "최초 로그인 시 user 정보 주는 경우에만 포함",
        requiredMode = RequiredMode.NOT_REQUIRED,
    )
    val email: String?,
    @Schema(title = "기기 ID", example = "0f365b39-c33d-39be-bdfc-74aaf55", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val deviceId: String,
    @Schema(title = "기기 타입", example = "ANDROID or IOS", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val deviceType: DevicePlatform,
)
