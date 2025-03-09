package com.ssak3.timeattack.fixture

import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.domain.OAuthProvider
import com.ssak3.timeattack.member.repository.entity.OAuthProviderInfo
import com.ssak3.timeattack.notifications.domain.PushNotification
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.domain.TaskKeywordsCombination
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskMode
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.domain.TaskType
import java.time.LocalDateTime

object Fixture {
    private val now = LocalDateTime.now()

    fun createMember(
        id: Long? = 1L,
        email: String = "test@example.com",
        nickname: String = "testUser",
        profileImageUrl: String = "https://test.com",
        oAuthProvider: OAuthProvider = OAuthProvider.KAKAO,
        subject: String = "1234567890",
        createdAt: LocalDateTime = now,
        updatedAt: LocalDateTime = now,
    ) = Member(
        id = id,
        email = email,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        oAuthProviderInfo =
            OAuthProviderInfo(
                oauthProvider = oAuthProvider,
                subject = subject,
            ),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    fun createTaskType(
        id: Int = 1,
        name: String = "프로그래밍",
    ) = TaskType(
        id = id,
        name = name,
    )

    fun createTaskMode(
        id: Int = 1,
        name: String = "긴급한",
    ) = TaskMode(
        id = id,
        name = name,
    )

    fun createTaskKeywordsCombination(
        taskType: TaskType = createTaskType(),
        taskMode: TaskMode = createTaskMode(),
    ) = TaskKeywordsCombination(
        taskType = taskType,
        taskMode = taskMode,
    )

    fun createPersona(
        id: Long = 1L,
        name: String = "Urgent Programmer",
        taskKeywordsCombination: TaskKeywordsCombination = createTaskKeywordsCombination(),
    ) = Persona(
        id = id,
        name = name,
        taskKeywordsCombination = taskKeywordsCombination,
    )

    fun createTask(
        id: Long? = 1L,
        name: String = "Test Task",
        category: TaskCategory = TaskCategory.URGENT,
        dueDatetime: LocalDateTime = now.plusDays(1),
        triggerAction: String? = null,
        triggerActionAlarmTime: LocalDateTime? = null,
        estimatedTime: Int? = null,
        status: TaskStatus = TaskStatus.BEFORE,
        member: Member = createMember(),
        persona: Persona = createPersona(),
        createdAt: LocalDateTime = now,
        updatedAt: LocalDateTime = now,
        isDeleted: Boolean = false,
    ) = Task(
        id = id,
        name = name,
        category = category,
        dueDatetime = dueDatetime,
        triggerAction = triggerAction,
        triggerActionAlarmTime = triggerActionAlarmTime,
        estimatedTime = estimatedTime,
        status = status,
        member = member,
        persona = persona,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
    )

    fun createUrgentTask(
        id: Long? = 1L,
        name: String = "Urgent Test Task",
        dueDatetime: LocalDateTime = now.plusDays(1),
        status: TaskStatus = TaskStatus.FOCUSED,
        member: Member = createMember(),
        persona: Persona = createPersona(),
        createdAt: LocalDateTime = now,
        updatedAt: LocalDateTime = now,
        isDeleted: Boolean = false,
    ) = Task(
        id = id,
        name = name,
        category = TaskCategory.URGENT,
        dueDatetime = dueDatetime,
        triggerAction = null,
        triggerActionAlarmTime = null,
        estimatedTime = null,
        status = status,
        member = member,
        persona = persona,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
    )

    /**
     * 다음 조건의 Scheduled Task 생성
     * <br>
     * - 마감시간은 현재시간보다 1일 뒤
     * - 예상 소요 시간은 1시간
     * - 작은 행동 알림 시간은 현재시간보다 10시간 뒤
     */
    fun createScheduledTask(
        id: Long? = 1L,
        name: String = "Test Task",
        category: TaskCategory = TaskCategory.SCHEDULED,
        dueDatetime: LocalDateTime = now.plusDays(1),
        triggerAction: String = "Trigger Action",
        triggerActionAlarmTime: LocalDateTime = now.plusHours(10),
        estimatedTime: Int = 60,
        status: TaskStatus = TaskStatus.BEFORE,
        member: Member = createMember(),
        persona: Persona = createPersona(),
        createdAt: LocalDateTime = now,
        updatedAt: LocalDateTime = now,
        isDeleted: Boolean = false,
    ) = Task(
        id = id,
        name = name,
        category = category,
        dueDatetime = dueDatetime,
        triggerAction = triggerAction,
        triggerActionAlarmTime = triggerActionAlarmTime,
        estimatedTime = estimatedTime,
        status = status,
        member = member,
        persona = persona,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
    )

    fun createPushNotification(
        member: Member = createMember(),
        task: Task = createTask(),
        scheduledAt: LocalDateTime = LocalDateTime.now(),
        isDeleted: Boolean = false,
        order: Int = 0,
    ) = PushNotification(
        member = member,
        task = task,
        scheduledAt = scheduledAt,
        isDeleted = isDeleted,
        order = order,
    )
}
