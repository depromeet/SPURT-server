package com.ssak3.timeattack.member.service

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun getMemberById(id: Long): Member =
        memberRepository.findByIdOrThrow(id)?.let {
            Member.toDomain(it)
        } ?: throw IllegalArgumentException("Member not found")
}
