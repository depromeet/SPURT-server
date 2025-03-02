package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.member.repository.entity.QMemberEntity.memberEntity
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.controller.dto.ScheduledTaskCreateRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import com.ssak3.timeattack.task.service.events.DeleteTaskEvent
import com.ssak3.timeattack.task.service.events.ScheduledTaskSaveEvent
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ExtendWith(MockKExtension::class)
@ExtendWith(SpringExtension::class)
class TaskServiceTest(
    @Autowired private val taskService: TaskService,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val taskTypeRepository: TaskTypeRepository,
    @Autowired private val taskModeRepository: TaskModeRepository,
    @Autowired private val personaRepository: PersonaRepository,
    @Autowired private val eventPublisher: ApplicationEventPublisher,
) {
    @Autowired
    private lateinit var taskRepository: TaskRepository
    private lateinit var member: Member

    @BeforeEach
    fun beforeEach() {
        val memberEntity =
            Fixture.createMember(
                id = null,
            ).toEntity()

        member = Member.fromEntity(memberRepository.saveAndFlush(memberEntity))
        val taskType = taskTypeRepository.saveAndFlush(TaskTypeEntity(name = "프로그래밍"))
        val taskMode = taskModeRepository.saveAndFlush(TaskModeEntity(name = "긴급한"))
        personaRepository.saveAndFlush(
            PersonaEntity(
                name = "Urgent Programmer",
                personaImageUrl = "https://testimage.com",
                taskType = taskType,
                taskMode = taskMode,
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
        clearMocks(eventPublisher)
    }

    @Test
    @DisplayName("urgent task 생성시 올바른 카테고리, Status, Persona를 가진 Task 생성된다.")
    fun createUrgentTaskTest() {
        // given
        val taskRequest =
            UrgentTaskRequest(
                "urgent task",
                LocalDateTime.now().plusDays(1),
                "프로그래밍",
                "긴급한",
            )

        // when
        val task = taskService.createUrgentTask(member, taskRequest)

        // then
        assertEquals(task.name, taskRequest.name)
        assertEquals(task.category, TaskCategory.URGENT)

        val savedTaskKeywordsCombination = task.persona.taskKeywordsCombination
        assertEquals(savedTaskKeywordsCombination.taskType.name, "프로그래밍")
        assertEquals(savedTaskKeywordsCombination.taskMode.name, "긴급한")
    }

    @Test
    @DisplayName("scheduled task 생성시 올바른 카테고리, Status, Persona를 가진 Task 생성되고, 이벤트가 발행된다.")
    fun createScheduledTaskTest() {
        // given
        val taskRequest =
            ScheduledTaskCreateRequest(
                name = "scheduled task",
                dueDatetime = LocalDateTime.now().plusDays(2),
                triggerAction = "trigger action",
                estimatedTime = 60,
                triggerActionAlarmTime = LocalDateTime.now().plusDays(1),
                taskType = "프로그래밍",
                taskMode = "긴급한",
            )

        every { eventPublisher.publishEvent(any()) } returns Unit

        // when
        val task = taskService.createScheduledTask(member, taskRequest)

        // then
        assertEquals(task.name, taskRequest.name)
        assertEquals(task.category, TaskCategory.SCHEDULED)

        val savedTaskKeywordsCombination = task.persona.taskKeywordsCombination
        assertEquals(savedTaskKeywordsCombination.taskType.name, "프로그래밍")
        assertEquals(savedTaskKeywordsCombination.taskMode.name, "긴급한")

        verify(exactly = 1) { eventPublisher.publishEvent(any<ScheduledTaskSaveEvent>()) }
    }

    @Test
    @DisplayName("이번 주 할 일 목록을 조회한다.")
    fun getTasksByDayOfWeekTest() {
        // given: 이번 주 월요일~일요일에 할 일 1개씩 저장
        val allTasks = mutableListOf<TaskEntity>()
        val today = LocalDate.now()
        val thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val thisSunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        var currentDate = thisMonday

        while (!currentDate.isAfter(thisSunday)) {
            val task =
                Fixture.createScheduledTask(
                    id = null,
                    dueDatetime = currentDate.atTime(22, 0),
                    triggerActionAlarmTime = currentDate.atTime(10, 0),
                ).toEntity()
            allTasks.add(task)
            currentDate = currentDate.plusDays(1)
        }
        taskRepository.saveAllAndFlush(allTasks)

        // when
        val tasksByDayOfWeek = taskService.getTasksForRestOfCurrentWeek(Fixture.createMember())

        // then
        when (today.dayOfWeek) {
            DayOfWeek.MONDAY -> assertThat(tasksByDayOfWeek).hasSize(6)
            DayOfWeek.TUESDAY -> assertThat(tasksByDayOfWeek).hasSize(5) // 수,목,금,토,일 (5개)
            DayOfWeek.WEDNESDAY -> assertThat(tasksByDayOfWeek).hasSize(4) // 목,금,토,일 (4개)
            DayOfWeek.THURSDAY -> assertThat(tasksByDayOfWeek).hasSize(3) // 금,토,일 (3개)
            DayOfWeek.FRIDAY -> assertThat(tasksByDayOfWeek).hasSize(2) // 토,일 (2개)
            DayOfWeek.SATURDAY -> assertThat(tasksByDayOfWeek).hasSize(1) // 일 (1개)
            DayOfWeek.SUNDAY -> assertThat(tasksByDayOfWeek).isEmpty() // 빈 목록 (0개)
        }
    }

    @Test
    @DisplayName("작업이 올바르게 삭제되고 작업 삭제 이벤트가 발행된다.")
    fun removeTaskTest() {
        // given
        val taskEntity =
            taskRepository.saveAndFlush(
                Fixture.createTask(
                    id = null,
                    member = member,
                ).toEntity(),
            )
        val taskId = checkNotNull(taskEntity.id)
        every { eventPublisher.publishEvent(any()) } returns Unit

        // when
        taskService.removeTask(member, taskId)

        // then
        val deletedTask = taskRepository.findByIdAndIsDeletedIsFalse(taskId)
        assertThat(deletedTask).isNull()

        verify(exactly = 1) { eventPublisher.publishEvent(any<DeleteTaskEvent>()) }
    }

    @TestConfiguration
    class MockitoPublisherConfiguration {
        @Bean
        @Primary
        fun publisher(): ApplicationEventPublisher = mockk(relaxed = true)
    }
}
