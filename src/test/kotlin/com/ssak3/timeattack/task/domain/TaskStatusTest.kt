package com.ssak3.timeattack.task.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TaskStatusTest {
    @ParameterizedTest
    @DisplayName("canTransitionTo 메서드는 현재 상태에서 이후 상태로의 전환을 허용한다")
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
        // when
        val result = initialStatus.canTransitionTo(newStatus)

        // then
        assertThat(result).isTrue()
    }

    @ParameterizedTest
    @DisplayName("canTransitionTo 메서드는 유효하지 않은 상태 전환 시 false를 반환한다")
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
        // when
        val result = initialStatus.canTransitionTo(newStatus)

        // then
        assertThat(result).isFalse()
    }
}
