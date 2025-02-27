package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import java.time.LocalDateTime

data class TaskStatusResponse(
    val id: Long,
    val name: String,
    val status: TaskStatus,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(task: Task): TaskStatusResponse {
            return TaskStatusResponse(
                id = task.id ?: throw IllegalStateException("id must not be null"),
                name = task.name,
                status = task.status,
                updatedAt = task.updatedAt ?: throw IllegalStateException("updatedAt must not be null"),
            )
        }
    }
}
