package com.ssak3.timeattack.task.service.events

data class DeleteTaskEvent(
    val memberId: Long,
    val taskId: Long,
)
