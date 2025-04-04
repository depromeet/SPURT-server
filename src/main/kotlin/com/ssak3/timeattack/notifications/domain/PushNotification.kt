package com.ssak3.timeattack.notifications.domain

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.notifications.repository.entity.PushNotificationEntity
import com.ssak3.timeattack.task.domain.Task
import java.time.LocalDateTime

class PushNotification(
    val member: Member,
    val task: Task,
    val scheduledAt: LocalDateTime,
    var isDeleted: Boolean = false,
    val order: Int,
    val message: String,
) {
    fun toEntity() =
        PushNotificationEntity(
            member = member.toEntity(),
            task = task.toEntity(),
            scheduledAt = scheduledAt,
            isDeleted = isDeleted,
            order = order,
            message = message,
        )

    fun delete() {
        isDeleted = true
    }

    companion object {
        fun fromEntity(entity: PushNotificationEntity) =
            PushNotification(
                member = Member.fromEntity(entity.member),
                task = Task.fromEntity(entity.task),
                scheduledAt = entity.scheduledAt,
                order = entity.order,
                message = entity.message,
            )
    }
}
