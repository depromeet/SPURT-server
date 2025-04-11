package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.external.firebase.domain.DevicePlatform
import com.ssak3.timeattack.notifications.client.MockServerClient
import com.ssak3.timeattack.notifications.domain.FcmMessage
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
    private val mockServerClient: MockServerClient,
) : Logger {
    @Scheduled(cron = "0 0/1 * * * ?") // 1분 단위 prod 테스트를 위해 임시로 변경
    fun sendNotifications() {
        val currentScheduledNotifications = pushNotificationService.getNotificationsByCurrentTime()
        logger.info("[ Push Notification Scheduler ] currentScheduledNotifications: $currentScheduledNotifications")
        currentScheduledNotifications.forEach {
            checkNotNull(it.member.id)
            checkNotNull(it.task.id)
            fcmDeviceService.getDevicesByMember(it.member.id).forEach { device ->
                // 응원문구(홈으로 가는 푸시 알림)은 task id 전달되면 안됨
                val taskId = if (it.order < 0) null else it.task.id
                val message =
                    FcmMessage(
                        token = device.fcmRegistrationToken,
                        platform = DevicePlatform.valueOf(device.devicePlatform.toString()),
                        taskId = taskId,
                        body = it.message,
                        route = getRoute(it.order),
                        title = it.task.name,
                    )

                mockServerClient.sendFcmMessageMock()
            }
        }
    }
}
