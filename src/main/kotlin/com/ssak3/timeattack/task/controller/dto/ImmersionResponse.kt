package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.task.domain.ImmersionTask

data class ImmersionResponse(
    val immersionTasks: List<ImmersionTask>,
)
