package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TaskTypeRepository : JpaRepository<TaskTypeEntity, Int> {
    fun findByName(name: String): TaskTypeEntity?
}
