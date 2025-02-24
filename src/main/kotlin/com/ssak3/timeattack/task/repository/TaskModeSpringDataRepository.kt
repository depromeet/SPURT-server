package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TaskModeSpringDataRepository : JpaRepository<TaskModeEntity, Int> {
    fun findByName(name: String): TaskModeEntity?
}
