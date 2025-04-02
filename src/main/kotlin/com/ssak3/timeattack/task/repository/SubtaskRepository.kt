package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.repository.entity.SubtaskEntity
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SubtaskRepository : JpaRepository<SubtaskEntity, Long> {
    fun findAllByTaskAndIsDeletedIs(
        task: TaskEntity,
        isDeleted: Boolean = false,
    ): List<SubtaskEntity>
}
