package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.common.domain.DevicePlatform
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.notifications.domain.FcmDevice
import com.ssak3.timeattack.notifications.repository.FcmDeviceRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class FcmDeviceServiceTest {
    @Mock
    private lateinit var fcmDeviceRepository: FcmDeviceRepository

    private lateinit var fcmDeviceService: FcmDeviceService

    @BeforeEach
    fun setup() {
        fcmDeviceService = FcmDeviceService(fcmDeviceRepository)
    }

    @Test
    @DisplayName("멤버와 FCM 토큰으로 등록된 디바이스가 없는 경우 새로운 디바이스를 저장한다")
    fun saveWhenDeviceNotExists() {
        // given
        val member = Fixture.createMember(id = 1L)
        val fcmToken = "test-fcm-token"
        val fcmDevice =
            FcmDevice(
                member = member,
                fcmRegistrationToken = fcmToken,
                devicePlatform = DevicePlatform.IOS,
                status = true,
            )

        `when`(fcmDeviceRepository.findActiveByMemberAndFcmToken(1L, fcmToken)).thenReturn(null)

        // when
        fcmDeviceService.save(fcmDevice)

        // then
        verify(fcmDeviceRepository, times(1)).findActiveByMemberAndFcmToken(1L, fcmToken)
        verify(fcmDeviceRepository, times(1)).save(any())
    }

    @Test
    @DisplayName("멤버와 FCM 토큰으로 등록된 디바이스가 이미 존재하는 경우 저장을 수행하지 않는다")
    fun saveWhenDeviceExists() {
        // given
        val member = Fixture.createMember(id = 1L)
        val fcmToken = "test-fcm-token"
        val fcmDevice =
            FcmDevice(
                member = member,
                fcmRegistrationToken = fcmToken,
                devicePlatform = DevicePlatform.IOS,
                status = true,
            )

        val fcmEntity = fcmDevice.toEntity()
        `when`(fcmDeviceRepository.findActiveByMemberAndFcmToken(1L, fcmToken)).thenReturn(fcmEntity)

        // when
        fcmDeviceService.save(fcmDevice)

        // then
        verify(fcmDeviceRepository, times(1)).findActiveByMemberAndFcmToken(1L, fcmToken)
        verify(fcmDeviceRepository, never()).save(any())
    }
}
