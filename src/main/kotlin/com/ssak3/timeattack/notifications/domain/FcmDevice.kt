package com.ssak3.timeattack.notifications.domain

import com.ssak3.timeattack.common.domain.DevicePlatform
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.notifications.repository.entity.FcmDeviceEntity

class FcmDevice(
    val member: Member,
    val fcmRegistrationToken: String,
    val devicePlatform: DevicePlatform,
    val status: Boolean = true,
) {
    fun toEntity() =
        FcmDeviceEntity(
            member = member.toEntity(),
            fcmRegistrationToken = fcmRegistrationToken,
            devicePlatform = devicePlatform,
            status = status,
        )

    companion object {
        fun fromEntity(entity: FcmDeviceEntity) =
            FcmDevice(
                member = Member.fromEntity(entity.member),
                fcmRegistrationToken = entity.fcmRegistrationToken,
                devicePlatform = entity.devicePlatform,
                status = entity.status,
            )
    }
}
