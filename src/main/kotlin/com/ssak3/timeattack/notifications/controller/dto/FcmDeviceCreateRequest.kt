package com.ssak3.timeattack.notifications.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode

@Schema(description = "사용자 fcm 디바이스 정보 저장")
data class FcmDeviceCreateRequest(
    @Schema(
        title = "디바이스 fcm token",
        requiredMode = RequiredMode.REQUIRED,
    )
    val fcmRegistrationToken: String,
    @Schema(
        title = "디바이스 type",
        allowableValues = ["IOS", "ANDROID"],
        requiredMode = RequiredMode.REQUIRED,
    )
    val deviceType: String,
)
