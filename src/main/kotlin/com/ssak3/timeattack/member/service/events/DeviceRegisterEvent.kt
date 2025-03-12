package com.ssak3.timeattack.member.service.events

import com.ssak3.timeattack.common.domain.DevicePlatform

data class DeviceRegisterEvent(
    val memberId: Long,
    val fcmRegistrationToken: String,
    val deviceType: DevicePlatform,
)
