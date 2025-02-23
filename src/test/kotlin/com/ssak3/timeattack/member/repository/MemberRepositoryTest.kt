package com.ssak3.timeattack.member.repository

import com.ssak3.timeattack.common.config.QueryDslConfig
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.domain.OAuthProviderInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@Import(QueryDslConfig::class)
@ActiveProfiles("test")
class MemberRepositoryTest
    @Autowired
    constructor(
        private val memberRepository: MemberRepository,
    ) {
        private lateinit var member: Member

        @BeforeEach
        fun setMember() {
            // given
            val provider = OAuthProvider.KAKAO
            val subject = "1234567890"
            val nickname = "testUser"
            member =
                Member(
                    nickname = nickname,
                    email = "test@test.com",
                    profileImageUrl = "https://test.com",
                    oAuthProviderInfo = OAuthProviderInfo(oauthProvider = provider, subject = subject),
                )
        }

        @Test
        @DisplayName("주어진 subject, provider와 일치하는 유저가 존재하면 해당 유저 반환")
        fun test_findByProviderAndSubject_ShouldReturnExpectedMember() {
            // given
            // saveAndFlush() << 즉시 DB에 반영 -> 조회 시 영속성 컨텍스트 캐시가 아닌 실제 DB 상태를 보장하기 위해서
            memberRepository.saveAndFlush(member)

            // when
            val findMember =
                memberRepository.findByProviderAndSubject(
                    member.oAuthProviderInfo.oauthProvider,
                    member.oAuthProviderInfo.subject,
                )

            // then
            assertThat(findMember).isNotNull
            assertThat(findMember?.nickname).isEqualTo(member.nickname)
        }

        @Test
        @DisplayName("주어진 subject, provider와 일치하는 유저가 없으면 null 반환")
        fun test_findByProviderAndSubject_ShouldReturnNull() {
            // given
            memberRepository.saveAndFlush(member)

            // when
            val findMember =
                memberRepository.findByProviderAndSubject(
                    member.oAuthProviderInfo.oauthProvider,
                    "different subject",
                )

            // then
            assertThat(findMember).isNull()
        }

        @Test
        @DisplayName("자동으로 생성된 BaseEntity의 audit 정보가 정상적으로 저장되는지 확인")
        fun test_baseEntityAudit() {
            // given
            memberRepository.saveAndFlush(member)

            // when
            val findMember =
                memberRepository.findByProviderAndSubject(
                    member.oAuthProviderInfo.oauthProvider,
                    member.oAuthProviderInfo.subject,
                )

            // then
            println("createdAt: ${findMember?.createdAt}, updatedAt: ${findMember?.updatedAt}")
        }
    }
