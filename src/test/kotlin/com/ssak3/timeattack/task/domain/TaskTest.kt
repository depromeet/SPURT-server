package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.TASK_INVALID_STATE_TRANSITION
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.task.domain.TaskStatus.BEFORE
import com.ssak3.timeattack.task.domain.TaskStatus.COMPLETE
import com.ssak3.timeattack.task.domain.TaskStatus.FOCUSED
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class TaskTest {
    private lateinit var urgentTask: Task
    private lateinit var scheduledTask: Task

    @BeforeEach
    fun setUp() {
        urgentTask = Fixture.createTask()
        scheduledTask = Fixture.createScheduledTask()
    }

    @Test
    @DisplayName("changeStatus 메서드는 정상적인 상태 변환 시 예외를 발생하지 않는다")
    fun allowValidTransition() {
        // given
        urgentTask.status = BEFORE

        // when
        urgentTask.changeStatus(FOCUSED)

        // then
        assertThat(urgentTask.status).isEqualTo(FOCUSED)
    }

    @Test
    @DisplayName("changeStatus 메서드는 유효하지 않은 상태 전환 시 예외를 발생시킨다")
    fun throwExceptionForInvalidTransition() {
        // given
        urgentTask.status = COMPLETE

        // when & then
        assertThrows<ApplicationException> {
            urgentTask.changeStatus(BEFORE)
        }.apply {
            assertThat(this.exceptionType).isEqualTo(TASK_INVALID_STATE_TRANSITION)
        }
    }

    @Test
    @DisplayName("assertOwnedBy 메서드는 Task의 멤버와 다른 사용자가 수정을 시도할 경우 예외를 발생시킨다")
    fun throwExceptionForNonTaskOwner() {
        // given
        val otherMemberId = 999L

        // when & then
        assertThrows<ApplicationException> {
            urgentTask.assertOwnedBy(otherMemberId)
        }.apply {
            assertThat(this.exceptionType).isEqualTo(ApplicationExceptionType.TASK_OWNER_MISMATCH)
        }
    }

    @Test
    @DisplayName("ScheduledTask의 trigger action 알람 시간으로부터 마감 시간까지의 시간이 예상 시간보다 작을 경우 예외가 발생한다.")
    fun throwExceptionForInvalidTriggerActionAlarmTime() {
        // given
        val now = LocalDateTime.now()
        val scheduledTask =
            Fixture.createScheduledTask(
                dueDatetime = now.plusMinutes(100),
                estimatedTime = 60,
            )
        val triggerActionAlarmTime = now.plusMinutes(50)

        // when & then
        assertThrows<ApplicationException> {
            scheduledTask.validateTriggerActionAlarmTime(triggerActionAlarmTime)
        }.apply {
            assertThat(this.exceptionType).isEqualTo(ApplicationExceptionType.INVALID_TRIGGER_ACTION_ALARM_TIME)
        }
    }

    @Test
    @DisplayName("ScheduledTask의 trigger action 알람 시간으로부터 마감 시간까지의 시간이 예상 소요 시간 이상일 경우 예외가 발생하지 않는다.")
    fun allowValidTriggerActionAlarmTime() {
        // given
        val now = LocalDateTime.now()
        val scheduledTask =
            Fixture.createScheduledTask(
                dueDatetime = now.plusMinutes(100),
                estimatedTime = 60,
            )
        val triggerActionAlarmTime = now.plusMinutes(30)

        // when & then
        assertDoesNotThrow {
            scheduledTask.validateTriggerActionAlarmTime(triggerActionAlarmTime)
        }
    }

    @Test
    @DisplayName("URGENT Task에 대해서 Trigger Action 알람을 검증할 시 예외가 발생한다.")
    fun throwExceptionForValidateTriggerActionAlarmTimeOnUrgentTask() {
        // given
        // when & then
        assertThrows<ApplicationException> {
            urgentTask.validateTriggerActionAlarmTime(LocalDateTime.now())
        }.apply {
            assertThat(this.exceptionType).isEqualTo(ApplicationExceptionType.TASK_CATEGORY_MISMATCH)
        }
    }

    @Test
    @DisplayName("마감시간 이후의 리마인더 알림을 검증할 경우 예외가 발생한다.")
    fun throwExceptionForValidateTriggerActionAlarmTimeAfterDueDatetime() {
        // given
        val now = LocalDateTime.now()
        val scheduledTask =
            Fixture.createScheduledTask(
                dueDatetime = now.plusMinutes(40),
                estimatedTime = 20,
            )
        val reminderNotificationTime = now.plusMinutes(50)

        // when & then
        assertThrows<ApplicationException> {
            scheduledTask.validateReminderAlarmTime(reminderNotificationTime)
        }.apply {
            assertThat(this.exceptionType).isEqualTo(ApplicationExceptionType.INVALID_REMINDER_ALARM_TIME)
        }
    }

    @Test
    @DisplayName("마감시간 이전의 리마인더 알림을 검증할 경우 예외가 발생하지 않는다.")
    fun allowValidReminderAlarmTimeBeforeDueDatetime() {
        // given
        val now = LocalDateTime.now()
        val scheduledTask =
            Fixture.createScheduledTask(
                dueDatetime = now.plusMinutes(40),
                estimatedTime = 20,
            )
        val reminderAlarmTime = now.plusMinutes(30)

        // when & then
        assertDoesNotThrow {
            scheduledTask.validateReminderAlarmTime(reminderAlarmTime)
        }
    }
}
