package com.ssak3.timeattack.notifications.repository

import com.ssak3.timeattack.notifications.repository.entity.PushNotificationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PushNotificationRepository : JpaRepository<PushNotificationEntity, Long>, PushNotificationRepositoryCustom
