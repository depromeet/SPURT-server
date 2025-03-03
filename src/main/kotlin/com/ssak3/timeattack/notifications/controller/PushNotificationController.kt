package com.ssak3.timeattack.notifications.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.external.firebase.domain.DevicePlatform
import com.ssak3.timeattack.notifications.controller.dto.PushNotificationSendRequest
import com.ssak3.timeattack.notifications.domain.FcmMessage
import com.ssak3.timeattack.notifications.service.FcmPushNotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/push-notifications")
class PushNotificationController(
    private val fcmPushNotificationService: FcmPushNotificationService,
) {
    // 프론트 푸시 알림 테스트용 api 제공
    @Operation(summary = "푸시 알림 전송 테스트", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PostMapping("/send")
    fun sendNotifications(
        @RequestBody request: PushNotificationSendRequest,
    ): ResponseEntity<String> {
        val response =
            fcmPushNotificationService.sendNotification(
                FcmMessage(
                    token = request.token,
                    platform = DevicePlatform.valueOf(request.platform),
                    body = request.body,
                    route = request.route,
                ),
            )

        return ResponseEntity.ok(response)
    }
}
