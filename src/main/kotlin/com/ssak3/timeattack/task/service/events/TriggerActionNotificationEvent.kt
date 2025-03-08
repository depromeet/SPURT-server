package com.ssak3.timeattack.task.service.events

import java.time.LocalDateTime

open class TriggerActionNotificationEvent(
    val memberId: Long,
    val taskId: Long,
    val alarmTime: LocalDateTime,
)
