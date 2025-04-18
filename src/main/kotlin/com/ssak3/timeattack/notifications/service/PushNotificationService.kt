package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.notifications.domain.PushNotification
import com.ssak3.timeattack.notifications.repository.PushNotificationRepository
import com.ssak3.timeattack.task.domain.Task
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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

    fun getNotificationsByCurrentTime(): List<PushNotification> {
        return pushNotificationRepository.findActiveAndScheduledAt(
            datetime = LocalDateTime.now().withSecond(0).withNano(0),
        ).map(PushNotification::fromEntity)
    }

    @Transactional
    fun delete(task: Task) {
        val now = LocalDateTime.now()
        val entities = pushNotificationRepository.findAllByTaskAndScheduledAtIsAfter(task.toEntity(), now)
        entities.forEach { e ->
            val notification = PushNotification.fromEntity(e)
            notification.delete()
            pushNotificationRepository.save(notification.toEntity())
        }
    }
}
