package com.ssak3.timeattack.notifications.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.notifications.repository.entity.FcmDeviceEntity
import com.ssak3.timeattack.notifications.repository.entity.QFcmDeviceEntity
import org.springframework.stereotype.Repository

@Repository
class FcmDeviceRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : FcmDeviceRepositoryCustom {
    private val fcmDevice = QFcmDeviceEntity.fcmDeviceEntity

    override fun findActiveByMember(memberId: Long): List<FcmDeviceEntity> {
        return queryFactory.selectFrom(fcmDevice)
            .where(
                fcmDevice.member.id.eq(memberId),
                fcmDevice.status.isTrue,
            ).fetch()
    }

    override fun existActiveByMemberAndFcmToken(
        memberId: Long,
        fcmToken: String,
    ): Boolean {
        val result =
            queryFactory.selectFrom(fcmDevice)
                .where(
                    fcmDevice.member.id.eq(memberId),
                    fcmDevice.fcmRegistrationToken.eq(fcmToken),
                    fcmDevice.status.isTrue,
                ).fetch()

        return result.size > 0
    }
}
