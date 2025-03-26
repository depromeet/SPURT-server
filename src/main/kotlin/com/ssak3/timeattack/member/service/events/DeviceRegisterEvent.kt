package com.ssak3.timeattack.member.service.events

import com.ssak3.timeattack.common.domain.DevicePlatform

// TODO: 삭제 예정
data class DeviceRegisterEvent(
    val memberId: Long,
    val fcmRegistrationToken: String,
    val deviceType: DevicePlatform,
)
