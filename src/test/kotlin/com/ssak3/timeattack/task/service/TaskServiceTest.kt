package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.controller.dto.ScheduledTaskCreateRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import com.ssak3.timeattack.task.service.events.ScheduledTaskSaveEvent
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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
    @Autowired private val eventPublisher: ApplicationEventPublisher
) {
    private lateinit var member: Member

    @BeforeEach
    fun beforeEach() {
        // given
        val provider = OAuthProvider.KAKAO
        val subject = "1234567890"
        val nickname = "testUser"
        val memberEntity =
            MemberEntity(
                nickname = nickname,
                email = "test@test.com",
                profileImageUrl = "https://test.com",
                oAuthProviderInfo = OAuthProviderInfo(oauthProvider = provider, subject = subject),
            )

        member = Member.fromEntity(memberRepository.saveAndFlush(memberEntity))
        val taskType = taskTypeRepository.saveAndFlush(TaskTypeEntity(name = "프로그래밍"))
        val taskMode = taskModeRepository.saveAndFlush(TaskModeEntity(name = "긴급한"))
        personaRepository.saveAndFlush(
            PersonaEntity(
                name = "Happy Programmer",
                personaImageUrl = "https://testimage.com",
                taskType = taskType,
                taskMode = taskMode,
            ),
        )
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

        // when
        val task = taskService.createScheduledTask(member, taskRequest)

        // then
        assertEquals(task.name, taskRequest.name)
        assertEquals(task.category, TaskCategory.SCHEDULED)

        val savedTaskKeywordsCombination = task.persona.taskKeywordsCombination
        assertEquals(savedTaskKeywordsCombination.taskType.name, "프로그래밍")
        assertEquals(savedTaskKeywordsCombination.taskMode.name, "긴급한")

        verify { eventPublisher.publishEvent(any<ScheduledTaskSaveEvent>()) }

    }

    @TestConfiguration
    class MockitoPublisherConfiguration {

        @Bean
        @Primary
        fun publisher(): ApplicationEventPublisher = mockk(relaxed = true)
    }

}
