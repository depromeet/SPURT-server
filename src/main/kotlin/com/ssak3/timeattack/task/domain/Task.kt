package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.persona.domain.Persona
import java.time.LocalDateTime

data class Task(
    val id: Long,
    val name: String,
    val category: TaskCategory,
    val dueDatetime: LocalDateTime,
    val triggerAction: String? = null,
    val estimatedTime: Int? = null,
    val status: TaskStatus,
    val member: Member,
    val persona: Persona,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isDeleted: Boolean,
)
