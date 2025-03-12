package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import java.time.LocalDateTime

data class TaskResponse(
    val id: Long? = null,
    val name: String,
    val category: String,
    val dueDatetime: LocalDateTime,
    val triggerAction: String? = null,
    val triggerActionAlarmTime: LocalDateTime? = null,
    val estimatedTime: Int? = null,
    val status: TaskStatus,
    val persona: Persona,
    val createdAt: LocalDateTime? = null,
) {
    companion object {
        fun fromTask(task: Task): TaskResponse {
            return TaskResponse(
                id = task.id,
                name = task.name,
                category = task.category.name,
                dueDatetime = task.dueDatetime,
                triggerAction = task.triggerAction,
                triggerActionAlarmTime = task.triggerActionAlarmTime,
                estimatedTime = task.estimatedTime,
                status = task.status,
                persona = task.persona,
                createdAt = task.createdAt,
            )
        }
    }
}
