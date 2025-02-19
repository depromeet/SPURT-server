package com.ssak3.timeattack.member.infrastructure

import com.ssak3.timeattack.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>, MemberRepositoryCustom
