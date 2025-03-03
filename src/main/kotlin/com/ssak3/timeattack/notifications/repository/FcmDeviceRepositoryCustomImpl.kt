package com.ssak3.timeattack.notifications.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.notifications.repository.entity.FcmDeviceEntity
import com.ssak3.timeattack.notifications.repository.entity.QFcmDeviceEntity
import org.springframework.stereotype.Repository

@Repository
class FcmDeviceRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : FcmDeviceRepositoryCustom {
    private val fcmDevice = QFcmDeviceEntity.fcmDeviceEntity

    override fun findActiveByMember(member: MemberEntity): List<FcmDeviceEntity> {
        return queryFactory.selectFrom(fcmDevice)
            .where(
                fcmDevice.member.id.eq(member.id),
                fcmDevice.status.isTrue,
            ).fetch()
    }
}
