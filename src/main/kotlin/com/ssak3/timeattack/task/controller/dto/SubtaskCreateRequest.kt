package com.ssak3.timeattack.task.controller.dto

data class SubtaskCreateRequest(
    val id: Long = 0,
    val taskId: Long,
    val name: String,
)
