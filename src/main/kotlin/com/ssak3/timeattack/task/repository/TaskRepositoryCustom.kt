package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.repository.entity.TaskEntity
import java.time.LocalDateTime

interface TaskRepositoryCustom {
    fun getTasksBetweenDates(
        memberId: Long,
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<TaskEntity>
}
