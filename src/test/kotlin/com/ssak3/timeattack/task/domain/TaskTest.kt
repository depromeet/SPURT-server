package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.global.exception.ApplicationExceptionType.TASK_INVALID_STATE_TRANSITION
import com.ssak3.timeattack.task.domain.TaskStatus.BEFORE
import com.ssak3.timeattack.task.domain.TaskStatus.COMPLETE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TaskTest {
    private val task: Task = Fixture.createTask()

    @Test
    @DisplayName("changeStatus 메서드는 정상적인 상태 변환 시 예외를 발생하지 않는다")
    fun allowValidTransition() {
        // given
        task.status = BEFORE

        // when
        task.changeStatus(COMPLETE)

        // then
        assertThat(task.status).isEqualTo(COMPLETE)
    }

    @Test
    @DisplayName("changeStatus 메서드는 유효하지 않은 상태 전환 시 예외를 발생시킨다")
    fun throwExceptionForInvalidTransition() {
        // given
        task.status = COMPLETE

        // when & then
        assertThrows<ApplicationException> {
            task.changeStatus(BEFORE)
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
