package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.notifications.domain.PushNotification
import com.ssak3.timeattack.notifications.repository.PushNotificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PushNotificationService(
    private val pushNotificationRepository: PushNotificationRepository,
) {
    @Transactional
    fun upsert(pushNotification: PushNotification) = pushNotificationRepository.save(pushNotification.toEntity())

    @Transactional
    fun upsertAll(pushNotifications: List<PushNotification>): Boolean {
        val entities = pushNotifications.map { it.toEntity() }
        val result = pushNotificationRepository.saveAll(entities)
        return result.size > 0
    }
}
