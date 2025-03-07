package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.external.firebase.domain.DevicePlatform
import com.ssak3.timeattack.notifications.domain.FcmMessage
import com.ssak3.timeattack.notifications.domain.FcmNotificationConstants.getMessage
import com.ssak3.timeattack.notifications.domain.FcmNotificationConstants.getRoute
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
@ConditionalOnProperty(name = ["fcm.scheduler.status"], havingValue = "true")
class PushNotificationScheduler(
    private val pushNotificationService: PushNotificationService,
    private val fcmDeviceService: FcmDeviceService,
    private val fcmPushNotificationService: FcmPushNotificationService,
) {
    @Async
    @Scheduled(cron = "0 0/5 * * * ?") //5분 단위
    fun sendNotifications() {
        val currentScheduledNotifications = pushNotificationService.getNotificationsByCurrentTime()
        currentScheduledNotifications.forEach {
            checkNotNull(it.member.id)
            checkNotNull(it.task.id)
            fcmDeviceService.getDevicesByMember(it.member.id).forEach { device ->
                val message =
                    FcmMessage(
                        token = device.fcmRegistrationToken,
                        platform = DevicePlatform.valueOf(device.devicePlatform.toString()),
                        taskId = it.task.id,
                        body = getMessage(it.order),
                        route = getRoute(it.order),
                    )

                fcmPushNotificationService.sendNotification(message)
            }
        }
    }
}
