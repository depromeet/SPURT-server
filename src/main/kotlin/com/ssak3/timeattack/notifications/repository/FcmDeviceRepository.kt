package com.ssak3.timeattack.notifications.repository

import com.ssak3.timeattack.notifications.repository.entity.FcmDeviceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FcmDeviceRepository : JpaRepository<FcmDeviceEntity, Long>, FcmDeviceRepositoryCustom
