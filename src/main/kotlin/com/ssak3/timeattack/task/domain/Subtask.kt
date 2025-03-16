package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.task.repository.entity.SubtaskEntity

class Subtask(
    val id: Long = 0,
    val task: Task,
    var name: String,
    var isDeleted: Boolean = false,
    var isCompleted: Boolean = false,
) {
    fun toEntity() =
        SubtaskEntity(
            task = task.toEntity(),
            name = name,
            isDeleted = isDeleted,
            isCompleted = isCompleted,
        )

    fun delete() {
        this.isDeleted = true
    }

    fun modifyName(name: String) {
        this.name = name
    }

    fun changeStatus() {
        this.isCompleted = !this.isCompleted
    }

    companion object {
        fun fromEntity(e: SubtaskEntity) =
            Subtask(
                id = e.id,
                task = Task.fromEntity(e.task),
                name = e.name,
                isDeleted = e.isDeleted,
                isCompleted = e.isCompleted,
            )
    }
}
