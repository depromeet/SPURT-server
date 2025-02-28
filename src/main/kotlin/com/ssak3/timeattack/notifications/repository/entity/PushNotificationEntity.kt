package com.ssak3.timeattack.notifications.repository.entity

import com.ssak3.timeattack.common.domain.BaseEntity
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "push_notifications")
class PushNotificationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val member: MemberEntity,
    @ManyToOne
    @JoinColumn(name = "task_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val task: TaskEntity,
    @Column(name = "scheduled_at")
    val scheduledAt: LocalDateTime,
    @Column(name = "is_deleted")
    val isDeleted: Boolean,
    @Column(name = "`order`")
    val order: Int,
) : BaseEntity()
