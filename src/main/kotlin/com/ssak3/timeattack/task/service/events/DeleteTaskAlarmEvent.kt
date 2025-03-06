package com.ssak3.timeattack.task.service.events

data class DeleteTaskAlarmEvent(
    val memberId: Long,
    val taskId: Long,
)
