package com.ssak3.timeattack.notifications.repository.entity

import com.ssak3.timeattack.common.domain.BaseEntity
import com.ssak3.timeattack.common.domain.DevicePlatform
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "fcm_devices",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_member_token",
            columnNames = ["member_id", "fcm_registration_token"],
        ),
    ],
)
class FcmDeviceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val member: MemberEntity,
    @Column(name = "fcm_registration_token", length = 500)
    val fcmRegistrationToken: String,
    @Column(name = "device_platform", length = 20)
    @Enumerated(value = EnumType.STRING)
    val devicePlatform: DevicePlatform,
    @Column(name = "status", columnDefinition = "TINYINT")
    val status: Boolean,
) : BaseEntity()
