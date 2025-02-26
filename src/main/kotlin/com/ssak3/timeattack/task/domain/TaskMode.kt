package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.task.repository.entity.TaskModeEntity

class TaskMode(
    val id: Int,
    val name: String,
) {
    fun toEntity(): TaskModeEntity {
        return TaskModeEntity(
            id = id,
            name = name,
        )
    }

    companion object {
        fun fromEntity(taskModeEntity: TaskModeEntity): TaskMode {
            return TaskMode(
                id = taskModeEntity.id ?: throw IllegalStateException("id must not be null"),
                name = taskModeEntity.name,
            )
        }
    }
}
