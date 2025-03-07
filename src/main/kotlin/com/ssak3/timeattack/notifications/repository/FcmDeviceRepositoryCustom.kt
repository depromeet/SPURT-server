package com.ssak3.timeattack.notifications.repository

import com.ssak3.timeattack.notifications.repository.entity.FcmDeviceEntity

interface FcmDeviceRepositoryCustom {
    fun findActiveByMember(memberId: Long): List<FcmDeviceEntity>
}
