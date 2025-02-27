package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import java.time.LocalDateTime

data class TaskResponse (
    val id: Long,
    val name: String,
    val category: String,
    val dueDatetime: LocalDateTime,
    val triggerAction: String? = null,
    val estimatedTime: Int? = null,
    val status: TaskStatus,
    val persona: Persona,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun fromTask(task: Task): TaskResponse {
            return TaskResponse(
                id = task.id ?: throw IllegalStateException("id must not be null"),
                name = task.name,
                category = task.category.name,
                dueDatetime = task.dueDatetime,
                triggerAction = task.triggerAction,
                estimatedTime = task.estimatedTime,
                status = task.status,
                persona = task.persona,
                createdAt = task.createdAt ?: throw IllegalStateException("createdAt must not be null"),
            )
        }
    }
}
