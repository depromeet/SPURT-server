package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.notifications.domain.FcmDevice
import com.ssak3.timeattack.notifications.repository.FcmDeviceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmDeviceService(
    private val fcmDeviceRepository: FcmDeviceRepository,
) {
    @Transactional
    fun save(fcmDevice: FcmDevice) {
        fcmDeviceRepository.save(fcmDevice.toEntity())
    }

    fun getDevicesByMember(member: Member): List<FcmDevice> =
        fcmDeviceRepository.findActiveByMember(member.toEntity()).map(FcmDevice::fromEntity)
}
