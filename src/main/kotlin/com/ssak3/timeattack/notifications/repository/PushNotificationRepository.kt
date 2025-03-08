package com.ssak3.timeattack.notifications.repository

import com.ssak3.timeattack.notifications.repository.entity.PushNotificationEntity
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PushNotificationRepository : JpaRepository<PushNotificationEntity, Long>, PushNotificationRepositoryCustom {
    fun findAllByTaskIs(task: TaskEntity): List<PushNotificationEntity>
}
