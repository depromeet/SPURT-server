package com.ssak3.timeattack.task.service.events

data class DeleteTaskNotificationEvent(
    val memberId: Long,
    val taskId: Long,
)
