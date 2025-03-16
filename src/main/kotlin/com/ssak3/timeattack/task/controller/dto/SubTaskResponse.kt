package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.task.domain.Subtask

data class SubTaskResponse(
    val id: Long,
    val taskId: Long,
    val name: String,
    val isDeleted: Boolean,
    val isCompleted: Boolean,
) {
    companion object {
        fun fromSubtask(subtask: Subtask) =
            SubTaskResponse(
                id = subtask.id,
                taskId = checkNotNull(subtask.task.id, "task id"),
                name = subtask.name,
                isDeleted = subtask.isDeleted,
                isCompleted = subtask.isCompleted,
            )
    }
}
