package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.controller.dto.ScheduledTaskCreateRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskStatus
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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
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
    private lateinit var member2: Member

    @BeforeEach
    fun beforeEach() {
        val memberEntity =
            Fixture.createMember(
                id = null,
            ).toEntity()
        val memberEntity2 =
            Fixture.createMember(
                id = null,
                subject = "123123123123"
            ).toEntity()

        member = Member.fromEntity(memberRepository.saveAndFlush(memberEntity))
        member2 = Member.fromEntity(memberRepository.saveAndFlush(memberEntity2))
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
    @DisplayName("여러 작업 중 알림 시간이 가장 최근인 작업을 반환한다")
    fun findAbandonedOrIgnoredTasksTest() {
        // given
        val now = LocalDateTime.now()

        // 1. WARMING_UP 상태인 작업 (이탈한 작업)
        val warmingUpTask =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.WARMING_UP,
                triggerActionAlarmTime = now.minusMinutes(4),
                dueDatetime = now.plusDays(1),
                member = member,
            ).toEntity()

        // 2. BEFORE 상태이고 알림이 3분 지난 작업 (무시된 작업 - 가장 최근)
        val ignoredTask1 =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.BEFORE,
                triggerActionAlarmTime = now.minusMinutes(3),
                dueDatetime = now.plusDays(2),
                member = member,
            ).toEntity()

        // 3. BEFORE 상태이고 알림이 5분 지난 작업 (무시된 작업 - 더 이전)
        val ignoredTask2 =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.BEFORE,
                triggerActionAlarmTime = now.minusMinutes(5),
                dueDatetime = now.plusHours(5),
                member = member,
            ).toEntity()

        // 4. BEFORE 상태이지만 알림이 아직 3분 지나지 않은 작업 (조회되면 안 됨)
        val notIgnoredTask =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.BEFORE,
                triggerActionAlarmTime = now.minusMinutes(2),
                dueDatetime = now.plusHours(3),
                member = member,
            ).toEntity()

        taskRepository.saveAllAndFlush(
            listOf(
                warmingUpTask,
                ignoredTask1,
                ignoredTask2,
                notIgnoredTask,
            ),
        )

        // when
        val foundTask = taskService.getAbandonedOrIgnoredTasks(member)
        // then
        assertThat(foundTask).isNotNull

        // 가장 최근에 알림이 발생한 ignoredTask1이 조회되어야 함
        assertThat(foundTask?.id).isEqualTo(ignoredTask1.id)
    }

    @Test
    @DisplayName("WARMING_UP 상태의 최근 알림 작업이 다른 작업보다 우선 반환된다")
    fun findMostRecentWarmingUpTask() {
        // given
        val now = LocalDateTime.now()

        // WARMING_UP 상태이고 알림 시간이 매우 최근인 작업 (푸시 알림 받고 앱들어갔다가 바로 나갔다가, 다시 홈화면 들어온 경우)
        val warmingUpTask =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.WARMING_UP,
                triggerActionAlarmTime = now.minusMinutes(1),
                dueDatetime = now.plusDays(1),
                member = member,
            ).toEntity()

        // BEFORE 상태이고 알림 시간이 3분 전인 작업 (알람 무시한지 3분이 된 경우)
        val beforeTask =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.BEFORE,
                triggerActionAlarmTime = now.minusMinutes(3),
                dueDatetime = now.plusDays(2),
                member = member,
            ).toEntity()

        taskRepository.saveAllAndFlush(listOf(warmingUpTask, beforeTask))

        // when
        val foundTask = taskService.getAbandonedOrIgnoredTasks(member)

        // then
        assertThat(foundTask).isNotNull
        // 알림 시간이 더 최근인 WARMING_UP 작업이 조회되어야 함
        assertThat(foundTask?.id).isEqualTo(warmingUpTask.id)
    }

    @Test
    @DisplayName("알림 시간이 동일할 경우 마감일이 더 빠른 작업을 반환한다")
    fun findTaskWithSameAlarmTimeTest() {
        // given
        val now = LocalDateTime.now()
        val sameAlarmTime = now.minusMinutes(5)

        // 알림 시간이 같은 두 작업 생성, 마감시간만 다름
        val earlierDueTask =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.BEFORE,
                triggerActionAlarmTime = sameAlarmTime,
                dueDatetime = now.plusDays(2),
                member = member,
            ).toEntity()

        val laterDueTask =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.BEFORE,
                triggerActionAlarmTime = sameAlarmTime,
                dueDatetime = now.plusDays(3),
                member = member,
            ).toEntity()

        taskRepository.saveAllAndFlush(
            listOf(
                earlierDueTask,
                laterDueTask,
            ),
        )

        // when
        val foundTask = taskService.getAbandonedOrIgnoredTasks(member)

        // then
        assertThat(foundTask).isNotNull
        assertThat(foundTask?.id).isEqualTo(earlierDueTask.id)
    }

    @Test
    @DisplayName("이탈한 작업이 없으면 null을 반환한다")
    fun findNoAbandonedOrIgnoredTasksTest() {
        // given
        val now = LocalDateTime.now()

        // 조건에 해당하지 않는 작업만 생성
        // 1. BEFORE 상태이지만 알림이 아직 3분 지나지 않은 작업
        val beforeTask =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.BEFORE,
                triggerActionAlarmTime = now.minusMinutes(2),
                dueDatetime = now.plusDays(1),
                member = member,
            ).toEntity()

        // 2. FOCUSED 상태인 작업
        val focusedTask =
            Fixture.createScheduledTask(
                id = null,
                status = TaskStatus.FOCUSED,
                triggerActionAlarmTime = now.minusMinutes(10),
                dueDatetime = now.plusDays(1),
                member = member,
            ).toEntity()

        taskRepository.saveAllAndFlush(listOf(beforeTask, focusedTask))

        // when
        val foundTask = taskService.getAbandonedOrIgnoredTasks(member)

        // then
        assertThat(foundTask).isNull()
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

    @Test
    @DisplayName("소유하지 않은 사용자에 의해 작업 조회 시 예외가 발생한다.")
    fun assertOwnedByTest() {
        // given
        val taskEntity =
            taskRepository.saveAndFlush(
                Fixture.createTask(
                    id = null,
                    member = member,
                ).toEntity(),
            )
        val taskId = checkNotNull(taskEntity.id)

        // when & then
        assertThrows<ApplicationException> { taskService.findTaskByIdAndMember(member2, taskId) }
            .apply {
                assertThat(exceptionType).isEqualTo(ApplicationExceptionType.TASK_OWNER_MISMATCH)
            }
    }

    @TestConfiguration
    class MockitoPublisherConfiguration {
        @Bean
        @Primary
        fun publisher(): ApplicationEventPublisher = mockk(relaxed = true)
    }
}
