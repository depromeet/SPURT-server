package com.ssak3.timeattack.task.service.events

import java.time.LocalDateTime

data class SupportNotificationSaveEvent(
    val memberId: Long,
    val taskId: Long,
    val alarmTimes: List<SupportAlarm>,
)

data class SupportAlarm(
    // index = 푸시 알림 문구 순서 (https://docs.google.com/spreadsheets/d/17fkS2WrMMJIFwEgZFnz5omtZDTuAriKYPaDmUf86zVQ/edit?gid=0#gid=0)
    val index: Int,
    val alarmTime: LocalDateTime,
)
