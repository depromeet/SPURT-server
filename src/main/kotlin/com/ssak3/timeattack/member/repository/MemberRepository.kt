package com.ssak3.timeattack.member.repository

import com.ssak3.timeattack.member.repository.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<MemberEntity, Long>, MemberRepositoryCustom
