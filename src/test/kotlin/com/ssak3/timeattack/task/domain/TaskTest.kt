package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.global.exception.ApplicationExceptionType.TASK_INVALID_STATE_TRANSITION
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.domain.TaskKeywordsCombination
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDateTime

class TaskTest {
    private val now = LocalDateTime.now()
    private lateinit var member: Member
    private lateinit var persona: Persona
    private lateinit var task: Task

    @BeforeEach
    fun setUp() {
        member =
            Member(
                id = 1L,
                email = "test@example.com",
                nickname = "testUser",
                profileImageUrl = "https://test.com",
                oAuthProviderInfo =
                    OAuthProviderInfo(
                        oauthProvider = OAuthProvider.KAKAO,
                        subject = "1234567890",
                    ),
                createdAt = now,
                updatedAt = now,
            )
        val taskKeywordsCombination =
            TaskKeywordsCombination(TaskType(name = "프로그래밍", id = 1), TaskMode(name = "즐거운", id = 1))

        persona =
            Persona(
                id = 1L,
                name = "Work Persona",
                taskKeywordsCombination = taskKeywordsCombination,
                personaImageUrl = "https://testimage.com",
            )

        task =
            Task(
                id = 1L,
                name = "Test Task",
                category = TaskCategory.URGENT,
                dueDatetime = now.plusDays(1),
                status = TaskStatus.BEFORE,
                member = member,
                persona = persona,
                createdAt = now,
                updatedAt = now,
            )
    }

    @ParameterizedTest
    @DisplayName("changeStatus 메서드는 현재 상태에서 이후 상태로의 전환을 허용한다")
    @CsvSource(
        value = [
            "BEFORE, WARMING_UP",
            "BEFORE, FOCUSED",
            "BEFORE, COMPLETE",
            "BEFORE, FAIL",
            "WARMING_UP, FOCUSED",
            "WARMING_UP, COMPLETE",
            "WARMING_UP, FAIL",
            "FOCUSED, COMPLETE",
        ],
    )
    fun allowValidTransition(
        initialStatus: TaskStatus,
        newStatus: TaskStatus,
    ) {
        // given
        task.status = initialStatus

        // when
        task.changeStatus(newStatus)

        // then
        assertThat(task.status).isEqualTo(newStatus)
    }

    @ParameterizedTest
    @DisplayName("changeStatus 메서드는 유효하지 않은 상태 전환 시 예외를 발생시킨다")
    @CsvSource(
        value = [
            "COMPLETE, BEFORE",
            "COMPLETE, WARMING_UP",
            "COMPLETE, FOCUSED",
            "COMPLETE, FAIL",
            "FAIL, BEFORE",
            "FAIL, WARMING_UP",
            "FAIL, FOCUSED",
            "FAIL, COMPLETE",
            "FOCUSED, BEFORE",
            "FOCUSED, WARMING_UP",
            "FOCUSED, FAIL",
            "WARMING_UP, BEFORE",
        ],
    )
    fun throwExceptionForInvalidTransition(
        initialStatus: TaskStatus,
        newStatus: TaskStatus,
    ) {
        // given
        task.status = initialStatus

        // when & then
        assertThrows<ApplicationException> {
            task.changeStatus(newStatus)
        }.apply {
            assertThat(this.exceptionType).isEqualTo(TASK_INVALID_STATE_TRANSITION)
        }
    }

    @Test
    @DisplayName("assertModifiableBy 메서드는 Task의 멤버와 다른 사용자가 수정을 시도할 경우 예외를 발생시킨다")
    fun throwExceptionForNonTaskOwner() {
        // given
        val otherMemberId = 999L

        // when
        assertThrows<ApplicationException> {
            task.assertModifiableBy(otherMemberId)
        }.apply {
            assertThat(this.exceptionType).isEqualTo(ApplicationExceptionType.TASK_MODIFICATION_NOT_ALLOWED_FOR_MEMBER)
        }
    }
}
