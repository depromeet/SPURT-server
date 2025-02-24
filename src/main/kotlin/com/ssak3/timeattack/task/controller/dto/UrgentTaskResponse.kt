package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.task.domain.Task
import java.time.LocalDateTime

data class UrgentTaskResponse(
    val id: Long,
    val name: String,
    val category: String,
    val dueDatetime: LocalDateTime,
    val persona: Persona,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(task: Task): UrgentTaskResponse {
            return UrgentTaskResponse(
                id = task.id ?: throw IllegalStateException("id must not be null"),
                name = task.name,
                category = task.category.name,
                dueDatetime = task.dueDatetime,
                persona = task.persona,
                createdAt = task.createdAt ?: throw IllegalStateException("createdAt must not be null"),
            )
        }
    }
}
