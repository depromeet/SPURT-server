package com.ssak3.timeattack.member.repository

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType.MEMBER_NOT_FOUND_BY_ID
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

interface MemberRepository : JpaRepository<MemberEntity, Long>, MemberRepositoryCustom

fun MemberRepository.findByIdOrThrow(id: Long): MemberEntity =
    findByIdOrNull(id) ?: throw ApplicationException(
        MEMBER_NOT_FOUND_BY_ID,
    )
