package com.ssak3.timeattack.task.service.events

import com.ssak3.timeattack.task.domain.TaskCategory
import java.time.LocalDateTime

data class ReminderSaveEvent (
    val alarmType: TaskCategory,
    val alarmTimes : List<ReminderAlarm>,
    val taskId: Long,
)

data class ReminderAlarm (
    val order: Int,
    val alarmTime: LocalDateTime,
)
