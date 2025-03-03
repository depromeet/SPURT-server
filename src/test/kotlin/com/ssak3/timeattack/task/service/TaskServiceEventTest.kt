package com.ssak3.timeattack.task.service

import com.ninjasquad.springmockk.MockkBean
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.notifications.service.PushNotificationListener
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * ApplicationEventPublisher의 실제 동작과 함께 TaskService 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ExtendWith(MockKExtension::class)
@ExtendWith(SpringExtension::class)
class TaskServiceEventTest(
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
}
