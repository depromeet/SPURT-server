package com.ssak3.timeattack.member.controller

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
    // TODO: 기기 정보, 기기 타입(ANDROID, IOS)
)
