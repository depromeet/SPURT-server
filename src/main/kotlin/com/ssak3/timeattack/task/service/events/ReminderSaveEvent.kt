package com.ssak3.timeattack.task.service.events

import java.time.LocalDateTime

data class ReminderSaveEvent(
    val memberId: Long,
    val taskId: Long,
    val alarmTimes: List<LocalDateTime>,
)
