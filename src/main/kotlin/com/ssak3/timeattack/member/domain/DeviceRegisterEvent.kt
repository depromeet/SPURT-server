package com.ssak3.timeattack.member.domain

data class DeviceRegisterEvent(
    val memberId: Long,
    val deviceId: String,
    val deviceType: DeviceType,
)
