package com.ssak3.timeattack.task.service

import com.ninjasquad.springmockk.MockkBean
import com.ssak3.timeattack.IntegrationTest
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.notifications.service.PushNotificationListener
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.controller.dto.TaskHoldOffRequest
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import com.ssak3.timeattack.task.service.events.ReminderSaveEvent
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.event.ApplicationEvents
import java.time.LocalDateTime

/**
 * ApplicationEventPublisher의 실제 동작과 함께 TaskService 테스트
 */
@IntegrationTest
class TaskServiceEventTest(
    @Autowired private val taskService: TaskService,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val taskTypeRepository: TaskTypeRepository,
    @Autowired private val taskModeRepository: TaskModeRepository,
    @Autowired private val personaRepository: PersonaRepository,
) {
    @Autowired
    private lateinit var taskRepository: TaskRepository
    private lateinit var member: Member
    private lateinit var persona: Persona

    @MockkBean
    private lateinit var pushNotificationListener: PushNotificationListener

    @BeforeEach
    fun beforeEach() {
        val memberEntity =
            Fixture.createMember(
                id = null,
            ).toEntity()

        member = Member.fromEntity(memberRepository.saveAndFlush(memberEntity))
        val taskType = taskTypeRepository.saveAndFlush(TaskTypeEntity(name = "프로그래밍"))
        val taskMode = taskModeRepository.saveAndFlush(TaskModeEntity(name = "긴급한"))
        persona =
            Persona.fromEntity(
                personaRepository.saveAndFlush(
                    PersonaEntity(
                        name = "Urgent Programmer",
                        taskType = taskType,
                        taskMode = taskMode,
                    ),
                ),
            )
    }

    @AfterEach
    fun clear() {
        taskRepository.deleteAll()
        personaRepository.deleteAll()
        taskModeRepository.deleteAll()
        taskTypeRepository.deleteAll()
        memberRepository.deleteAll()
    }

    @Test
    @DisplayName("작업 삭제시 이벤트 리스너에서 예외가 발생해도 정상적으로 작업이 삭제된다.")
    fun removeTaskWithEventListenerExceptionTest() {
        // given
        val taskEntity =
            taskRepository.saveAndFlush(
                Fixture.createTask(
                    id = null,
                    member = member,
                    persona = persona,
                ).toEntity(),
            )
        val taskId = checkNotNull(taskEntity.id)
        every { pushNotificationListener.deleteNotifications(any()) } throws RuntimeException()

        // when
        taskService.removeTask(member, taskId)

        // then
        val deletedTask = taskRepository.findByIdAndIsDeletedIsFalse(taskId)
        assertThat(deletedTask).isNull()
    }

    @Test
    @DisplayName("리마인더 설정 시 정확한 지정된 간격과 횟수로 정확한 알림 정보를 이벤트로 전달한다.")
    fun publishReminderSaveEventWithCorrectAlarm(events: ApplicationEvents) {
        // given
        val taskEntity =
            taskRepository.saveAndFlush(
                Fixture.createScheduledTask(
                    id = null,
                    member = member,
                    persona = persona,
                ).toEntity(),
            )
        val taskId = checkNotNull(taskEntity.id)

        val taskHoldOffRequest =
            TaskHoldOffRequest(
                remindInterval = 15,
                remindCount = 3,
                remindBaseTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
            )

        every { pushNotificationListener.saveNotifications(any()) } returns Unit

        // when
        taskService.holdOffTask(taskId, member, taskHoldOffRequest)

        // then
        val eventList =
            events.stream(ReminderSaveEvent::class.java)
                .toList()
        assertThat(eventList).hasSize(1)

        val reminderSaveEvent = eventList[0]
        assertThat(reminderSaveEvent.memberId).isEqualTo(member.id)
        assertThat(reminderSaveEvent.taskId).isEqualTo(taskId)

        val expectedReminderAlarms =
            listOf(
                LocalDateTime.of(2025, 1, 1, 0, 15, 0),
                LocalDateTime.of(2025, 1, 1, 0, 30, 0),
                LocalDateTime.of(2025, 1, 1, 0, 45, 0),
            )
        assertThat(reminderSaveEvent.alarmTimes).containsExactlyElementsOf(expectedReminderAlarms)
    }
}
