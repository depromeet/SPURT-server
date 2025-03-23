package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.external.firebase.domain.DevicePlatform
import com.ssak3.timeattack.notifications.domain.FcmMessage
import com.ssak3.timeattack.notifications.domain.FcmNotificationConstants.getMessage
import com.ssak3.timeattack.notifications.domain.FcmNotificationConstants.getRoute
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
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
) : Logger {
    @Scheduled(cron = "0 0/1 * * * ?") // 1분 단위 prod 테스트를 위해 임시로 변경
    fun sendNotifications() {
        val currentScheduledNotifications = pushNotificationService.getNotificationsByCurrentTime()
        logger.info("[ Push Notification Scheduler ] currentScheduledNotifications: $currentScheduledNotifications")
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
