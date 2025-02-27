package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import java.time.LocalDateTime

data class TaskStatusResponse(
    val id: Long,
    val name: String,
    val currentStatus: TaskStatus,
    val beforeStatus: TaskStatus,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(
            task: Task,
            beforeStatus: TaskStatus,
        ): TaskStatusResponse {
            return TaskStatusResponse(
                id = task.id ?: throw IllegalStateException("id must not be null"),
                name = task.name,
                currentStatus = task.status,
                beforeStatus = beforeStatus,
                updatedAt = task.updatedAt ?: throw IllegalStateException("updatedAt must not be null"),
            )
        }
    }
}
