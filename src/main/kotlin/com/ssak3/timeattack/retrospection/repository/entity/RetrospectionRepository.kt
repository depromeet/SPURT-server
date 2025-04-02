package com.ssak3.timeattack.retrospection.repository.entity

import org.springframework.data.jpa.repository.JpaRepository

interface RetrospectionRepository : JpaRepository<RetrospectionEntity, Long> {
    fun findAllByMemberId(memberId: Long): List<RetrospectionEntity>
}
