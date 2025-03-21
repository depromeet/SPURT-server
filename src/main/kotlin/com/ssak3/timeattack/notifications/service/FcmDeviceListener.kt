package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.member.service.MemberService
import com.ssak3.timeattack.member.service.events.DeviceRegisterEvent
import com.ssak3.timeattack.notifications.domain.FcmDevice
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class FcmDeviceListener(
    private val fcmDeviceService: FcmDeviceService,
    private val memberService: MemberService,
) {
    @EventListener
    fun save(event: DeviceRegisterEvent) {
        val member = memberService.getMemberById(event.memberId)
        val fcmDevice =
            FcmDevice(
                member = member,
                fcmRegistrationToken = event.fcmRegistrationToken,
                devicePlatform = event.deviceType,
            )
        fcmDeviceService.save(fcmDevice)
    }
}
