package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.task.repository.entity.SubtaskEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SubtaskRepository : JpaRepository<SubtaskEntity, Long>
