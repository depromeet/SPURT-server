package com.ssak3.timeattack.notifications.controller

import com.ssak3.timeattack.notifications.controller.dto.PushNotificationCreateRequest
import com.ssak3.timeattack.notifications.domain.PushNotification
import com.ssak3.timeattack.notifications.service.PushNotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/push-notifications")
class PushNotificationController (
    private val pushNotificationService: PushNotificationService,
) {

    // TODO("Swagger 설정 후 반영 예정")
    @PostMapping("/create")
    fun createNotifications(
        @RequestBody body: PushNotificationCreateRequest,
    ): ResponseEntity<Boolean> {
        val pushNotifications: List<PushNotification> = body.scheduledDates.map { date ->
            PushNotification(
                memberId = body.memberId,
                taskId = body.taskId,
                scheduledAt = date,
            )
        }

        val result = pushNotificationService.upsertAll(pushNotifications)
        return ResponseEntity.ok(result)
    }
}
