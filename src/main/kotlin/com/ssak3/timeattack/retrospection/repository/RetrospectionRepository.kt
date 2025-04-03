package com.ssak3.timeattack.retrospection.repository

import com.ssak3.timeattack.retrospection.repository.entity.RetrospectionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RetrospectionRepository : JpaRepository<RetrospectionEntity, Long>, RetrospectionRepositoryCustom
