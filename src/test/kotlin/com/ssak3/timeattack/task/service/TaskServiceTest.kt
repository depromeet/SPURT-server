package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.controller.dto.UrgentTaskRequest
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.repository.TaskModeRepository
import com.ssak3.timeattack.task.repository.TaskTypeRepository
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceTest(
    @Autowired private val taskService: TaskService,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val taskTypeRepository: TaskTypeRepository,
    @Autowired private val taskModeRepository: TaskModeRepository,
    @Autowired private val personaRepository: PersonaRepository,
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
}
