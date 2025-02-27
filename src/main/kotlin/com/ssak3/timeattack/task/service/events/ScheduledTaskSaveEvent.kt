package com.ssak3.timeattack.task.service.events

import java.time.LocalDateTime

class ScheduledTaskSaveEvent(
    val memberId: Long,
    val taskId: Long,
    val alarmTime: LocalDateTime,
)
