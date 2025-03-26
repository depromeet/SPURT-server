package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.common.domain.DevicePlatform
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.notifications.controller.dto.FcmDeviceCreateRequest
import com.ssak3.timeattack.notifications.domain.FcmDevice
import com.ssak3.timeattack.notifications.repository.FcmDeviceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmDeviceService(
    private val fcmDeviceRepository: FcmDeviceRepository,
) {
    @Transactional
    fun save(
        member: Member,
        request: FcmDeviceCreateRequest,
    ) {
        val memberId = checkNotNull(member.id)

        // 해당 유저의 기기가 이미 등록되어 있으면 등록하지 않음
        val isExist =
            fcmDeviceRepository.existActiveByMemberAndFcmToken(
                memberId = memberId,
                fcmToken = request.fcmRegistrationToken,
            )

        if (!isExist) {
            val fcmDevice =
                FcmDevice(
                    member = member,
                    fcmRegistrationToken = request.fcmRegistrationToken,
                    devicePlatform = DevicePlatform.valueOf(request.deviceType),
                )
            fcmDeviceRepository.save(fcmDevice.toEntity())
        }
    }

    fun getDevicesByMember(memberId: Long): List<FcmDevice> =
        fcmDeviceRepository.findActiveByMember(memberId).map(FcmDevice::fromEntity)
}
