package com.ssak3.timeattack.notifications.repository

import com.ssak3.timeattack.notifications.repository.entity.PushNotificationEntity
import java.time.LocalDateTime

interface PushNotificationRepositoryCustom {
    fun findActiveAndScheduledAt(datetime: LocalDateTime): List<PushNotificationEntity>
}
