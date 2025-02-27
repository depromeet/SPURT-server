package com.ssak3.timeattack.task.service.events

import com.ssak3.timeattack.task.domain.TaskCategory
import java.time.LocalDateTime

data class ReminderSaveEvent(
    val memberId: Long,
    val taskId: Long,
    val alarmTimes: List<ReminderAlarm>,
)

data class ReminderAlarm(
    val order: Int,
    val alarmTime: LocalDateTime,
)
