package com.ssak3.timeattack.task.domain

import java.time.LocalDateTime

class ImmersionTask(
    val taskId: Long,
    val taskName: String,
    val dueDatetime: LocalDateTime,
    val personaId: Long,
    val personaName: String,
    val subtasks: List<Subtask>,
    val playlistIds: List<String>,
)
