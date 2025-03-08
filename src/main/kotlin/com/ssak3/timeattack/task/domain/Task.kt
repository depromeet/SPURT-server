package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import java.time.LocalDateTime

class Task(
    val id: Long? = null,
    var name: String,
    val category: TaskCategory,
    var dueDatetime: LocalDateTime,
    var triggerAction: String? = null,
    var estimatedTime: Int? = null,
    var triggerActionAlarmTime: LocalDateTime? = null,
    var status: TaskStatus,
    val member: Member,
    val persona: Persona,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    var isDeleted: Boolean = false,
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
            isDeleted = isDeleted,
        )

    /**
     * 입력된 triggerActionAlarmTime 이 기존 task 의 estimatedTime 과 dueDatetime 에 대해 유효한지 검증
     */
    fun validateTriggerActionAlarmTime(triggerActionAlarmTime: LocalDateTime) {
        validateTriggerActionAlarmTime(triggerActionAlarmTime, this.estimatedTime, this.dueDatetime)
    }

    /**
     * 입력된 마감시간과 작은 행동 알림 시간이 이 기존 task 의 예상 소요 시간에 대해 유효한지 검증
     */
    fun validateTriggerActionAlarmTime(
        triggerActionAlarmTime: LocalDateTime,
        dueDatetime: LocalDateTime,
    ) {
        validateTriggerActionAlarmTime(triggerActionAlarmTime, this.estimatedTime, dueDatetime)
    }

    /**
     * 입력된 예상 소요 시간과 작은 행동 알림 시간이 이 기존 task 의 마감시간에 대해 유효한지 검증
     */
    fun validateTriggerActionAlarmTime(
        triggerActionAlarmTime: LocalDateTime,
        estimatedTime: Int,
    ) {
        validateTriggerActionAlarmTime(triggerActionAlarmTime, estimatedTime, this.dueDatetime)
    }

    /**
     * 작은 행동 알림 시간을 검증합니다.
     * 작은 행동 알림시간에 예상 소요 시간(estimatedTime)을 더한 시간이 마감 시간(dueDatetime)보다 뒤에 있으면 예외를 발생시킵니다.
     */
    fun validateTriggerActionAlarmTime(
        triggerActionAlarmTime: LocalDateTime,
        estimatedTime: Int?,
        dueDatetime: LocalDateTime,
    ) {
        // 현재 버퍼타임에 대한 계산을 프론트에서 해서 보여주기 때문에 서버에서는 정확한 버퍼타임에 대한 배수는 검증하지 않음
        // triggerActionAlarmTime부터 dueDatetime까지의 시간이 estimatedTime보다 커야 한다.
        if (category == TaskCategory.URGENT) {
            throw ApplicationException(ApplicationExceptionType.TASK_CATEGORY_MISMATCH, category)
        }
        checkNotNull(estimatedTime, "estimatedTime")
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
    fun assertOwnedBy(memberId: Long) {
        if (this.member.id != memberId) {
            throw ApplicationException(
                ApplicationExceptionType.TASK_OWNER_MISMATCH,
                checkNotNull(this.member.id),
                memberId,
            )
        }
    }

    fun delete() {
        this.isDeleted = true
    }

    fun modifyName(name: String) {
        this.name = name
    }

    fun modifyTriggerAction(triggerAction: String) {
        validateTaskStatusForUpdate(TaskStatus.BEFORE, "triggerAction")
        this.triggerAction = triggerAction
    }

    fun modifyEstimatedTime(
        estimatedTime: Int,
        triggerActionAlarmTime: LocalDateTime,
    ) {
        validateTaskStatusForUpdate(TaskStatus.BEFORE, "estimatedTime")
        validateTriggerActionAlarmTime(triggerActionAlarmTime, estimatedTime, this.dueDatetime)
        this.estimatedTime = estimatedTime
        this.triggerActionAlarmTime = triggerActionAlarmTime
    }

    fun modifyToUrgentDueDatetime(
        dueDatetime: LocalDateTime,
        triggerActionAlarmTime: LocalDateTime,
    ) {
        // BEFORE 상태에서만 수정 가능
        validateTaskStatusForUpdate(TaskStatus.BEFORE, "dueDatetime")
        validateTriggerActionAlarmTime(triggerActionAlarmTime, checkNotNull(this.estimatedTime), dueDatetime)
        this.dueDatetime = dueDatetime
        this.triggerActionAlarmTime = triggerActionAlarmTime
    }

    fun modifyToUrgentDueDatetime(dueDatetime: LocalDateTime) {
        validateTaskStatusForUpdate(TaskStatus.BEFORE, "dueDatetime")
        changeStatus(TaskStatus.FOCUSED)
        this.dueDatetime = dueDatetime
    }

    fun validateTaskStatusForUpdate(
        status: TaskStatus,
        attribute: String,
    ) {
        if (this.status != TaskStatus.BEFORE) {
            throw ApplicationException(ApplicationExceptionType.INVALID_TASK_STATUS_FOR_UPDATE, attribute, this.status)
        }
    }

    fun validateReminderAlarmTime(reminderAlarmTime: LocalDateTime) {
        if (reminderAlarmTime.isAfter(dueDatetime)) {
            throw ApplicationException(
                ApplicationExceptionType.INVALID_REMINDER_ALARM_TIME,
                reminderAlarmTime,
                dueDatetime,
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
