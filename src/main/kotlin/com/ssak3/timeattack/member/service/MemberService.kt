package com.ssak3.timeattack.member.service

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun getMemberById(id: Long): Member =
        memberRepository.findMemberById(id)?.let {
            Member.fromEntity(it)
        } ?: throw ApplicationException(ApplicationExceptionType.MEMBER_NOT_FOUND_BY_ID, id)
}
