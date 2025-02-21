package com.ssak3.timeattack.notifications.repository.entity

import com.ssak3.timeattack.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "push_notifications")
class PushNotificationEntity(
    @Id
    @GeneratedValue
    val id: Long = 0,
    @Column(name = "member_id")
    val memberId: Long,
    @Column(name = "task_id")
    val taskId: Long,
    @Column(name = "scheduled_at")
    val scheduledAt: LocalDateTime,
    val status: Boolean,
) : BaseEntity()
