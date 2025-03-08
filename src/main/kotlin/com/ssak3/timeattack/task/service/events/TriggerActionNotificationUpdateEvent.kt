package com.ssak3.timeattack.task.service.events

import java.time.LocalDateTime

class TriggerActionNotificationUpdateEvent(
    memberId: Long,
    taskId: Long,
    alarmTime: LocalDateTime,
) : TriggerActionNotificationEvent(
        memberId,
        taskId,
        alarmTime,
    )
