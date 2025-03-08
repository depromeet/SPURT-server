package com.ssak3.timeattack.notifications

import com.ssak3.timeattack.TimeApplicationTest
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.notifications.repository.PushNotificationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.test.assertEquals

@TimeApplicationTest
class PushNotificationRepositoryTest(
    @Autowired private val pushNotificationRepository: PushNotificationRepository,
) {
    @BeforeEach
    fun set() {
        (0..3).forEach {
            pushNotificationRepository.save(
                Fixture.createPushNotification(
                    scheduledAt = LocalDateTime.now().plusMinutes(it.toLong()).withSecond(0).withNano(0),
                    order = it,
                ).toEntity(),
            )
        }
    }

    @Test
    @DisplayName("현재 시간에 스케줄링된 데이터 반환")
    fun test_findActiveAndScheduledAt() {
        val now = LocalDateTime.now().withSecond(0).withNano(0)
        val notifications =
            pushNotificationRepository.findActiveAndScheduledAt(
                datetime = now,
            )

        assertEquals(1, notifications.size)
        assertEquals(0, notifications.get(0).order)
    }
}
