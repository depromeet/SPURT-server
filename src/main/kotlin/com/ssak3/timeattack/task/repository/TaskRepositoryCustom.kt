package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.repository.entity.TaskEntity
import java.time.LocalDate

interface TaskRepositoryCustom {
    fun getNextTwoTasksThisWeek(
        memberId: Long,
        start: LocalDate,
        end: LocalDate,
    ): List<TaskEntity>
}
