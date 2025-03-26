package com.ssak3.timeattack.retrospection.domain

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.retrospection.repository.entity.RetrospectionEntity
import com.ssak3.timeattack.task.domain.Task
import java.time.LocalDateTime

class Retrospection(
    val id: Long? = null,
    val member: Member,
    val task: Task,
    val satisfaction: Int,
    val concentration: Int,
    val comment: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
) {
    fun toEntity() =
        RetrospectionEntity(
            id = id,
            member = member.toEntity(),
            task = task.toEntity(),
            satisfaction = satisfaction,
            concentration = concentration,
            comment = comment,
        )

    companion object {
        fun fromEntity(entity: RetrospectionEntity) =
            Retrospection(
                id = entity.id,
                member = Member.fromEntity(entity.member),
                task = Task.fromEntity(entity.task),
                satisfaction = entity.satisfaction,
                concentration = entity.concentration,
                comment = entity.comment,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )
    }
}
