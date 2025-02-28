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
    val triggerActionAlarmTime: LocalDateTime? = null,
    var status: TaskStatus,
    val member: Member,
    val persona: Persona,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val isDeleted: Boolean = false,
) {
    fun toEntity() =
        TaskEntity(
            id = id,
            name = name,
            category = category,
            dueDatetime = dueDatetime,
            triggerAction = triggerAction,
            status = status,
            triggerActionAlarmTime = triggerActionAlarmTime,
            estimatedTime = estimatedTime,
            member = member.toEntity(),
            persona = persona.toEntity(),
        )

    fun validateTriggerActionAlarmTime(triggerActionAlarmTime: LocalDateTime) {
        // 현재 버퍼타임에 대한 계산을 프론트에서 해서 보여주기 때문에 서버에서는 정확한 버퍼타임에 대한 배수는 검증하지 않음
        // triggerActionAlarmTime은 마감 이전이어야 한다.
        // triggerActionAlarmTime부터 dueDatetime까지의 시간이 estimatedTime보다 커야 한다.

        if (category == TaskCategory.URGENT) {
            throw ApplicationException(ApplicationExceptionType.TASK_CATEGORY_MISMATCH, category)
        }
        val checkedEstimatedTime = (checkNotNull(estimatedTime) { "estimatedTime must not be null" }).toLong()
        if (triggerActionAlarmTime.plusMinutes(checkedEstimatedTime).isAfter(dueDatetime)) {
            throw ApplicationException(
                ApplicationExceptionType.INVALID_TRIGGER_ACTION_ALARM_TIME,
                triggerActionAlarmTime,
                dueDatetime,
                estimatedTime,
            )
        }
    }

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

    /**
     * Task 수정 가능한지 확인한다.
     */
    fun assertModifiableBy(memberId: Long) {
        if (this.member.id != memberId) {
            throw ApplicationException(
                ApplicationExceptionType.TASK_MODIFICATION_NOT_ALLOWED_FOR_MEMBER,
                this.member.id.toString(),
                memberId,
            )
        }
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
                triggerActionAlarmTime = entity.triggerActionAlarmTime,
                status = entity.status,
                member = Member.fromEntity(entity.member),
                persona = Persona.fromEntity(entity.persona),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isDeleted = entity.isDeleted,
            )
    }
}
