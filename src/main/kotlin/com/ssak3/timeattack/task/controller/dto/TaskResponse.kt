package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.task.domain.Task
import java.time.LocalDateTime

data class TaskResponse(
    val id: Long,
    val name: String,
    val dueDatetime: LocalDateTime,
    val estimatedTime: Int,
    val triggerActionAlarmTime: LocalDateTime? = null,
) {
    companion object {
        fun fromTask(task: Task): TaskResponse {
            return TaskResponse(
                id = task.id ?: throw IllegalStateException("id must not be null"),
                name = task.name,
                dueDatetime = task.dueDatetime,
                estimatedTime = task.estimatedTime ?: throw IllegalStateException("estimatedTime must not be null"),
                triggerActionAlarmTime = task.triggerActionAlarmTime,
            )
        }
    }
}
