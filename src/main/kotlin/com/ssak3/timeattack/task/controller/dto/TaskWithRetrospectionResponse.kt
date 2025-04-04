package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.retrospection.domain.Retrospection
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import java.time.LocalDateTime

data class TaskWithRetrospectionResponse(
    val id: Long? = null,
    val name: String,
    val personaId: Long,
    val personaName: String,
    val triggerAction: String? = null,
    val estimatedTime: Int? = null,
    val dueDateTime: LocalDateTime,
    val status: TaskStatus,
    val updatedAt: LocalDateTime? = null,
    val satisfaction: Int? = null,
    val concentration: Int? = null,
    val comment: String? = null,
) {
    companion object {
        fun fromTaskAndRetrospection(
            task: Task,
            retrospection: Retrospection,
        ): TaskWithRetrospectionResponse {
            return TaskWithRetrospectionResponse(
                id = task.id,
                name = task.name,
                personaId = task.persona.id,
                personaName = task.persona.name,
                triggerAction = task.triggerAction,
                estimatedTime = task.estimatedTime,
                dueDateTime = task.dueDatetime,
                status = task.status,
                updatedAt = task.updatedAt,
                satisfaction = retrospection.satisfaction,
                concentration = retrospection.concentration,
                comment = retrospection.comment,
            )
        }

        // Task만으로 생성하는 메서드 (회고 데이터 없는 경우)
        fun fromTaskOnly(task: Task): TaskWithRetrospectionResponse {
            return TaskWithRetrospectionResponse(
                id = task.id,
                name = task.name,
                personaId = task.persona.id,
                personaName = task.persona.name,
                triggerAction = task.triggerAction,
                estimatedTime = task.estimatedTime,
                dueDateTime = task.dueDatetime,
                status = task.status,
                updatedAt = task.updatedAt,
                satisfaction = null,
                concentration = null,
                comment = null,
            )
        }
    }
}
