package com.ssak3.timeattack.member.controller

import com.ssak3.timeattack.common.domain.DeviceType
import com.ssak3.timeattack.member.domain.OAuthProvider
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.NotBlank

@Schema(description = "소셜 로그인 요청")
data class LoginRequest(
    @Schema(title = "인가 코드", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val authCode: String,
    @Schema(title = "OAuth 제공자", example = "KAKAO", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val provider: OAuthProvider,
    @Schema(title = "기기 ID", example = "0f365b39-c33d-39be-bdfc-74aaf55", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val deviceId: String,
    @Schema(title = "기기 타입", example = "ANDROID or IOS", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val deviceType: DeviceType,
)
