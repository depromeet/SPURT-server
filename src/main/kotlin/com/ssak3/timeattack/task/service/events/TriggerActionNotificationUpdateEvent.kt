package com.ssak3.timeattack.task.service.events

import java.time.LocalDateTime

class TriggerActionNotificationUpdateEvent(
    val memberId: Long,
    val taskId: Long,
    val alarmTime: LocalDateTime,
)
