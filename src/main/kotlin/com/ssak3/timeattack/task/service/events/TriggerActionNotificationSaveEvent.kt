package com.ssak3.timeattack.task.service.events

import java.time.LocalDateTime

data class TriggerActionNotificationSaveEvent(
    val memberId: Long,
    val taskId: Long,
    val alarmTime: LocalDateTime,
)
