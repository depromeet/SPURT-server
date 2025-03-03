package com.ssak3.timeattack.notifications.repository

import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.notifications.repository.entity.FcmDeviceEntity

interface FcmDeviceRepositoryCustom {
    fun findActiveByMember(member: MemberEntity): List<FcmDeviceEntity>
}
