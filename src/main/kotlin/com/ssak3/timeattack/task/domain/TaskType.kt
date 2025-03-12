package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity

class TaskType(
    val id: Int,
    val name: String,
) {
    fun toEntity(): TaskTypeEntity {
        return TaskTypeEntity(
            id = id,
            name = name,
        )
    }

    companion object {
        fun fromEntity(taskTypeEntity: TaskTypeEntity): TaskType {
            return TaskType(
                id = taskTypeEntity.id ?: throw IllegalStateException("id must not be null"),
                name = taskTypeEntity.name,
            )
        }
    }
}
