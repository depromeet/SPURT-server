package com.ssak3.timeattack.task.service.events

import java.time.LocalDateTime

class TriggerActionNotificationSaveEvent(
    val memberId: Long,
    val taskId: Long,
    val alarmTimes: List<LocalDateTime>,
)
