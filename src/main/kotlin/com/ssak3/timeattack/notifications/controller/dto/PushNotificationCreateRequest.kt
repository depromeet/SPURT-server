package com.ssak3.timeattack.notifications.controller.dto

import java.time.LocalDateTime

data class PushNotificationCreateRequest (
    val memberId: Long,
    val taskId: Long,
    val scheduledDates: List<LocalDateTime>,
)