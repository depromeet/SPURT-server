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
    fun save(pushNotification: PushNotification) = pushNotificationRepository.save(pushNotification.toEntity())

    @Transactional
    fun saveAll(pushNotifications: List<PushNotification>) {
        val entities = pushNotifications.map { it.toEntity() }
        pushNotificationRepository.saveAll(entities)
    }
}
