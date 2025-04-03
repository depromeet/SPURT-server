package com.ssak3.timeattack.retrospection.repository

import com.ssak3.timeattack.retrospection.repository.entity.RetrospectionEntity

interface RetrospectionRepositoryCustom {
    fun findAllByMemberId(memberId: Long): List<RetrospectionEntity>
}
