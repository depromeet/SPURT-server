package com.ssak3.timeattack.task.repository

import com.ssak3.timeattack.common.config.QueryDslConfig
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.repository.MemberRepository
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskCategory.SCHEDULED
import com.ssak3.timeattack.task.domain.TaskCategory.URGENT
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.domain.TaskStatus.BEFORE
import com.ssak3.timeattack.task.domain.TaskStatus.COMPLETE
import com.ssak3.timeattack.task.domain.TaskStatus.FAIL
import com.ssak3.timeattack.task.domain.TaskStatus.FOCUSED
import com.ssak3.timeattack.task.domain.TaskStatus.PROCRASTINATING
import com.ssak3.timeattack.task.domain.TaskStatus.WARMING_UP
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
@Import(QueryDslConfig::class)
@ActiveProfiles("test")
class TaskRepositoryCustomImplTest(
    @Autowired private val taskRepository: TaskRepository,
    @Autowired private val memberRepository: MemberRepository,
) {
    private lateinit var member: MemberEntity

    @BeforeEach
    fun beforeEach() {
        member =
            memberRepository.save(
                Fixture.createMember(
                    id = null,
                ).toEntity(),
            )
    }

    @AfterEach
    fun afterEach() {
        taskRepository.deleteAll()
    }

    @Test
    @DisplayName("오늘 할 일 조회")
    fun test_findTodayTasks() {
        // given
        val taskCreateInfoList =
            listOf(
                // urgent
                TaskCreateInfo("urgent task1", URGENT, "2025-03-01 23:59:59", BEFORE),
                TaskCreateInfo("urgent task2", URGENT, "2025-03-01 23:59:59", FOCUSED),
                TaskCreateInfo("urgent task3", URGENT, "2025-02-28 23:59:59", COMPLETE),
                TaskCreateInfo("urgent task4", URGENT, "2025-02-28 23:59:59", FAIL),
                // scheduled
                TaskCreateInfo("scheduled task1", SCHEDULED, "2025-03-01 23:59:59", BEFORE, "2025-03-01 20:00:00"),
                TaskCreateInfo("scheduled task2", SCHEDULED, "2025-03-01 23:59:59", FOCUSED, "2025-03-01 20:00:00"),
                TaskCreateInfo("scheduled task3", SCHEDULED, "2025-03-02 23:59:59", FOCUSED, "2025-03-02 20:00:00"),
                TaskCreateInfo(
                    "scheduled task4",
                    SCHEDULED,
                    "2025-03-01 23:59:59",
                    PROCRASTINATING,
                    "2025-03-01 00:00:01",
                ),
                TaskCreateInfo(
                    "scheduled task5",
                    SCHEDULED,
                    "2025-03-02 23:59:59",
                    PROCRASTINATING,
                    "2025-03-01 00:00:01",
                ),
                TaskCreateInfo("scheduled task6", SCHEDULED, "2025-03-02 23:59:59", BEFORE, "2025-03-02 20:00:00"),
                TaskCreateInfo("scheduled task7", SCHEDULED, "2025-03-01 23:59:59", FOCUSED, "2025-02-28 20:00:00"),
                TaskCreateInfo("scheduled task8", SCHEDULED, "2025-02-28 23:59:59", COMPLETE, "2025-02-28 20:00:00"),
                TaskCreateInfo("scheduled task9", SCHEDULED, "2025-02-28 23:59:59", FAIL, "2025-03-01 20:00:00"),
                TaskCreateInfo("scheduled task10", SCHEDULED, "2025-03-01 23:59:59", WARMING_UP, "2025-03-01 00:00:01"),
            )

        // save task
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        taskCreateInfoList.forEachIndexed { index, taskCreateInfo ->
            val task =
                Fixture.createTask(
                    id = null,
                    name = taskCreateInfo.name,
                    category = taskCreateInfo.category,
                    member = Member.fromEntity(member),
                    dueDatetime = LocalDateTime.parse(taskCreateInfo.dueDatetime, formatter),
                    status = taskCreateInfo.status,
                    triggerActionAlarmTime =
                        taskCreateInfo.triggerActionAlarmTime?.let {
                                time ->
                            LocalDateTime.parse(time, formatter)
                        },
                )
            taskRepository.save(task.toEntity())
        }

        // when
        val tasks = taskRepository.findTodayTasks(checkNotNull(member.id), LocalDate.parse("2025-03-01"))

        // then
        assertEquals(8, tasks.size)

        val taskNames = tasks.map { it.name }
        assertTrue(
            taskNames.containsAll(
                listOf(
                    "urgent task2",
                    "scheduled task1",
                    "scheduled task2",
                    "scheduled task3",
                    "scheduled task4",
                    "scheduled task5",
                    "scheduled task7",
                    "scheduled task10",
                ),
            ),
        )
    }

    data class TaskCreateInfo(
        val name: String,
        val category: TaskCategory,
        val dueDatetime: String,
        val status: TaskStatus,
        val triggerActionAlarmTime: String? = null,
    )
}
