package com.ssak3.timeattack.member.infrastructure

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MemberRepository : JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.oAuthProviderInfo.subject = :subject AND m.oAuthProviderInfo.oauthProvider = :oAuthProvider")
    fun findBySubjectAndOAuthProvider(
        @Param("subject") subject: String,
        @Param("oAuthProvider") oAuthProvider: OAuthProvider
    ): Member
}