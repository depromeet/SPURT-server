package com.ssak3.timeattack.member.repository

import com.ssak3.timeattack.member.repository.entity.AuthTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AuthTokenRepository : JpaRepository<AuthTokenEntity, Long>
