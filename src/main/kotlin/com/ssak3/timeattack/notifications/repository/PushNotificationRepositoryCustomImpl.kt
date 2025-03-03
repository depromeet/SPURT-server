package com.ssak3.timeattack.notifications.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.notifications.repository.entity.PushNotificationEntity
import com.ssak3.timeattack.notifications.repository.entity.QPushNotificationEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PushNotificationRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : PushNotificationRepositoryCustom {
    private val pushNotification = QPushNotificationEntity.pushNotificationEntity

    override fun findActiveAndScheduledAt(datetime: LocalDateTime): List<PushNotificationEntity> {
        return queryFactory.selectFrom(pushNotification)
            .where(
                pushNotification.scheduledAt.eq(datetime),
                pushNotification.isDeleted.isFalse,
            ).fetch()
    }
}
