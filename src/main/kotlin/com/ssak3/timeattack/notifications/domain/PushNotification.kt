package com.ssak3.timeattack.notifications.domain

import com.ssak3.timeattack.notifications.repository.entity.PushNotificationEntity
import java.time.LocalDateTime

data class PushNotification(
    val id: Long = 0,
    val memberId: Long,
    val taskId: Long,
    val scheduledAt: LocalDateTime,
    val status: Boolean = true,
) {

    fun toEntity() = PushNotificationEntity(
        id = this.id,
        memberId = this.memberId,
        taskId = this.taskId,
        scheduledAt = this.scheduledAt,
        status = this.status,
    )
}
