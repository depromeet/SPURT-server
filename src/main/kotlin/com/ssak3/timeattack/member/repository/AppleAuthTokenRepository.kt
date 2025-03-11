package com.ssak3.timeattack.member.repository

import com.ssak3.timeattack.member.repository.entity.AppleAuthTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AppleAuthTokenRepository : JpaRepository<AppleAuthTokenEntity, Long>
