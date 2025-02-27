package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType
import com.ssak3.timeattack.global.exception.ApplicationExceptionType.TASK_INVALID_STATE_TRANSITION
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.controller.dto.ChangeTaskStatusRequest
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.domain.TaskStatus.WARMING_UP
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceTest(
    @Autowired private val taskService: TaskService,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val taskTypeRepository: TaskTypeRepository,
    @Autowired private val taskModeRepository: TaskModeRepository,
    @Autowired private val personaRepository: PersonaRepository,
) {
    @Autowired
    private lateinit var taskRepository: TaskRepository
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
        val taskMode = taskModeRepository.saveAndFlush(TaskModeEntity(name = "즐거운"))
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
                "즐거운",
            )

        // when
        val task = taskService.createUrgentTask(member, taskRequest)

        // then
        assertEquals(task.name, taskRequest.name)
        assertEquals(task.category, TaskCategory.URGENT)

        val savedTaskKeywordsCombination = task.persona.taskKeywordsCombination
        assertEquals(savedTaskKeywordsCombination.taskType.name, "프로그래밍")
        assertEquals(savedTaskKeywordsCombination.taskMode.name, "즐거운")
    }

    @Test
    @DisplayName("Task 상태를 BEFORE에서 WARMING_UP으로 성공적으로 변경한다")
    fun changeTaskStatusFromBeforeToWarmingUpTest() {
        // given
        val taskId = createTestTask()
        val request = ChangeTaskStatusRequest(status = WARMING_UP)

        // when
        taskService.changeTaskStatus(taskId, checkNotNull(member.id), request)

        // then
        val updatedTask = taskRepository.findById(taskId).get()
        assertEquals(WARMING_UP, updatedTask.status)
        assertEquals(taskId, updatedTask.id)
    }

    @Test
    @DisplayName("Task 상태를 한 단계 건너뛰어 BEFORE에서 FOCUSED로 성공적으로 변경한다")
    fun changeTaskStatusSkippingOneStateTest() {
        // given
        val taskId = createTestTask()
        val request = ChangeTaskStatusRequest(status = TaskStatus.FOCUSED)

        // when
        taskService.changeTaskStatus(taskId, checkNotNull(member.id), request)

        // then
        val updatedTask = taskRepository.findById(taskId).get()
        assertEquals(TaskStatus.FOCUSED, updatedTask.status)
        assertEquals(taskId, updatedTask.id)
    }

    @Test
    @DisplayName("기존 상태보다 이전 상태로 변경할 경우 예외가 발생한다")
    fun changeTaskStatusToPreviousStateThrowsExceptionTest() {
        // given
        val taskId = createTestTask()
        val request = ChangeTaskStatusRequest(status = TaskStatus.FOCUSED)

        taskService.changeTaskStatus(taskId, checkNotNull(member.id), request)

        val invalidRequest = ChangeTaskStatusRequest(status = WARMING_UP)

        // when & then: FOCUSED -> WARMING_UP 로 변경 시도
        assertThrows<ApplicationException> {
            taskService.changeTaskStatus(taskId, checkNotNull(member.id), invalidRequest)
        }.apply {
            assertEquals(this.exceptionType, TASK_INVALID_STATE_TRANSITION)
        }
    }

    @Test
    @DisplayName("다른 사용자가 Task 상태 변경을 시도하면 예외가 발생한다")
    fun changeTaskStatusByAnotherMemberThrowsExceptionTest() {
        // given
        val taskId = createTestTask()
        val otherMemberEntity =
            MemberEntity(
                nickname = "otherUser",
                email = "other@test.com",
                profileImageUrl = "https://test.com",
                oAuthProviderInfo =
                    OAuthProviderInfo(
                        oauthProvider = OAuthProvider.KAKAO,
                        subject = "9876543210",
                    ),
            )
        val otherMember = Member.fromEntity(memberRepository.saveAndFlush(otherMemberEntity))
        val request = ChangeTaskStatusRequest(status = WARMING_UP)

        // when & then
        assertThrows<ApplicationException> {
            taskService.changeTaskStatus(taskId, checkNotNull(otherMember.id), request)
        }.apply {
            assertEquals(this.exceptionType, ApplicationExceptionType.TASK_MODIFICATION_NOT_ALLOWED_FOR_MEMBER)
        }
    }

    /**
     * 테스트용 Task를 생성하는 헬퍼 메서드
     */
    private fun createTestTask(taskName: String = "urgent task"): Long {
        val taskRequest =
            UrgentTaskRequest(
                taskName,
                LocalDateTime.now().plusDays(1),
                "프로그래밍",
                "즐거운",
            )

        val task = taskService.createUrgentTask(member, taskRequest)
        return checkNotNull(task.id) { "Task ID는 생성 후 조회 시 null일 수 없습니다" }
    }
}
