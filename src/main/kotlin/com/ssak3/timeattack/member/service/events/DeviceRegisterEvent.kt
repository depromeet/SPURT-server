package com.ssak3.timeattack.member.service.events

import com.ssak3.timeattack.member.domain.DeviceType

data class DeviceRegisterEvent(
    val memberId: Long,
    val deviceId: String,
    val deviceType: DeviceType,
)
