package com.ssak3.timeattack.member.controller

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.NotBlank

@Schema(description = "JWT 토큰 재발급 요청")
data class RefreshRequest(
    @Schema(title = "refresh 토큰", requiredMode = RequiredMode.REQUIRED)
    @field:NotBlank
    val refreshToken: String,
)
