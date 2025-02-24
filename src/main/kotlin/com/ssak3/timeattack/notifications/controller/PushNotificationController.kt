package com.ssak3.timeattack.notifications.controller

import com.ssak3.timeattack.external.firebase.domain.DevicePlatform
import com.ssak3.timeattack.notifications.controller.dto.PushNotificationSendRequest
import com.ssak3.timeattack.notifications.domain.FcmMessage
import com.ssak3.timeattack.notifications.service.FcmPushNotificationService
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
    @PostMapping("/send")
    fun sendNotifications(
        @RequestBody request: PushNotificationSendRequest,
    ): ResponseEntity<String> {
        val response =
            fcmPushNotificationService.sendNotification(
                FcmMessage(
                    token = request.token,
                    platform = DevicePlatform.valueOf(request.platform),
                    title = request.title,
                    body = request.body,
                    route = request.route,
                ),
            )

        return ResponseEntity.ok(response)
    }
}
