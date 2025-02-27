package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
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
    var status: TaskStatus,
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
            member = member.toEntity(),
            persona = persona.toEntity(),
        )

    /**
     * Task의 상태를 변경한다.
     */
    fun changeStatus(newStatus: TaskStatus) {
        if (this.status == newStatus) return

        // 현재 상태에서 전환 가능한 상태인지 확인
        if (!this.status.canTransitionTo(newStatus)) {
            throw ApplicationException(ApplicationExceptionType.TASK_INVALID_STATE_TRANSITION, this.status, newStatus)
        }

        this.status = newStatus
    }

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
                member = Member.fromEntity(entity.member),
                persona = Persona.fromEntity(entity.persona),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isDeleted = entity.isDeleted,
            )
    }
}
