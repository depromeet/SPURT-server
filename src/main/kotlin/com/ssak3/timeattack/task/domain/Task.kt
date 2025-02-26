package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import java.time.LocalDateTime

class Task(
    val id: Long? = null,
    val name: String,
    val category: TaskCategory,
    val dueDatetime: LocalDateTime,
    val triggerAction: String? = null,
    val estimatedTime: Int? = null,
    val status: TaskStatus,
    val member: Member,
    val persona: Persona,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val isDeleted: Boolean = false,
) {
    fun toEntity() =
        TaskEntity(
            name = name,
            category = category,
            dueDatetime = dueDatetime,
            triggerAction = triggerAction,
            estimatedTime = estimatedTime,
            status = status,
            // TODO: MemberEntity로 변경
            member = member,
            persona = persona.toEntity(),
        )

    companion object {
        fun fromEntity(entity: TaskEntity) =
            Task(
                id = entity.id,
                name = entity.name,
                category = entity.category,
                dueDatetime = entity.dueDatetime,
                triggerAction = entity.triggerAction,
                estimatedTime = entity.estimatedTime,
                status = entity.status,
                member = entity.member,
                persona = Persona.fromEntity(entity.persona),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isDeleted = entity.isDeleted,
            )
    }
}
