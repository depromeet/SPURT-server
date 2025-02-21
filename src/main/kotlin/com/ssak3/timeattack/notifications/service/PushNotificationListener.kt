package com.ssak3.timeattack.notifications.service

import org.springframework.context.event.EventListener

class PushNotificationListener(
    private val pushNotificationService: PushNotificationService,
) {
    @EventListener
    fun saveNotification() {
        TODO("작업 저장 후 발송되는 이벤트를 받아 알림 저장")
    }
}
