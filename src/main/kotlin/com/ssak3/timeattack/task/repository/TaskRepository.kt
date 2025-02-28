package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.repository.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository : JpaRepository<TaskEntity, Long>, TaskRepositoryCustom
