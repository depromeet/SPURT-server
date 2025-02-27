package com.ssak3.timeattack.task.service.events

import com.ssak3.timeattack.task.domain.TaskCategory
import java.time.LocalDateTime

class ScheduledTaskSaveEvent(
    val memberId: Long,
    val taskId: Long,
    val alarmTime: LocalDateTime,
)
