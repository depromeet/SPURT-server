package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.repository.entity.SubtaskEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SubtaskRepository : JpaRepository<SubtaskEntity, Long> {
    fun findAllByTaskAndIsDeletedIs(
        task: Task,
        isDeleted: Boolean = false,
    ): List<SubtaskEntity>
}
