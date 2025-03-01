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

@DataJpaTest
@Import(QueryDslConfig::class)
@ActiveProfiles("test")
class TaskRepositoryCustomImplTest(
    @Autowired private val taskRepository: TaskRepository,
    @Autowired private val memberRepository: MemberRepository,
) {
    private lateinit var member: MemberEntity

    private final val todayDate: LocalDate = LocalDate.now()
    val tomorrowStartTime: LocalDateTime = todayDate.plusDays(1).atTime(0, 0, 0)
    val todayEndDateTime: LocalDateTime = todayDate.atTime(23, 59, 59)
    val yesterdayEndDateTime: LocalDateTime = todayDate.minusDays(1).atTime(23, 59, 59)
    val tomorrowEndDateTime: LocalDateTime = todayDate.plusDays(1).atTime(23, 59, 59)
    val today8Pm: LocalDateTime = todayDate.atTime(20, 0, 0)
    val yesterday8Pm: LocalDateTime = todayDate.minusDays(1).atTime(20, 0, 0)
    val tomorrow8Pm: LocalDateTime = todayDate.plusDays(1).atTime(20, 0, 0)
    val todayH00M00S01: LocalDateTime = todayDate.atTime(0, 0, 1)

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
                // urgent (name, category, dueDatetime, status)
                // 마감시간전, 즉시 시작 등록 후 이탈 (오늘 할 일 O)
                TaskCreateInfo("urgent task1", URGENT, todayEndDateTime, BEFORE),
                // 마감시간전, 즉시 시작 등록 후 몰입 시작 (오늘 할 일 O)
                TaskCreateInfo("urgent task2", URGENT, todayEndDateTime, FOCUSED),
                // 마감시간전, 즉시 시작 등록 후 작업 완료 (오늘 할 일 X)
                TaskCreateInfo("urgent task3", URGENT, todayEndDateTime, COMPLETE),
                // 마감시간후, 즉시 시작 등록 후 작업 완료 (오늘 할 일 X)
                TaskCreateInfo("urgent task4", URGENT, yesterdayEndDateTime, COMPLETE),
                // 마감시간후, 즉시 시작 등록 후 작업 실패 (오늘 할 일 X)
                TaskCreateInfo("urgent task5", URGENT, yesterdayEndDateTime, FAIL),
                // scheduled
                // 마감시간 전, 여유 있게 시작 등록 (오늘 할 일 O)
                TaskCreateInfo("scheduled task1", SCHEDULED, todayEndDateTime, BEFORE, today8Pm),
                // 마감시간 전, 여유 있게 시작 등록 후 몰입 시작 (오늘 할 일 O)
                TaskCreateInfo("scheduled task2", SCHEDULED, todayEndDateTime, FOCUSED, today8Pm),
                // 마감시간 전, 알람시간 전, 여유 있게 시작 등록 후 몰입 시작 (오늘 할 일 O)
                TaskCreateInfo("scheduled task3", SCHEDULED, tomorrowEndDateTime, FOCUSED, tomorrow8Pm),
                // 오늘 마감시간 전, 알람시간 후, 여유 있게 시작 등록 후 리마인더 알람 모두 미룸 (오늘 할 일 O)
                TaskCreateInfo(
                    "scheduled task4",
                    SCHEDULED,
                    todayEndDateTime,
                    PROCRASTINATING,
                    todayH00M00S01,
                ),
                // 내일 마감시간 전, 오늘 알람시간 후, 여유 있게 시작 작업 등록 후 리마인더 알람 모두 미룸 (오늘 할 일 O)
                TaskCreateInfo(
                    "scheduled task5",
                    SCHEDULED,
                    tomorrowEndDateTime,
                    PROCRASTINATING,
                    todayH00M00S01,
                ),
                // 내일 마감시간 전, 여유 있게 시작 작업 등록 (오늘 할 일X)
                TaskCreateInfo("scheduled task6", SCHEDULED, tomorrowEndDateTime, BEFORE, tomorrow8Pm),
                // [Scheduled Task] 오늘 마감시간 전, 어제 알림시간 후, 몰입 중 (오늘 할 일 O)
                TaskCreateInfo("scheduled task7", SCHEDULED, todayEndDateTime, FOCUSED, yesterday8Pm),
                // [Scheduled Task] 어제 마감시간 후, 어제 알림시간 후, 완료 (오늘 할 일 X)
                TaskCreateInfo("scheduled task8", SCHEDULED, yesterdayEndDateTime, COMPLETE, yesterday8Pm),
                // [Scheduled Task] 어제 마감시간 후, 어제 알림시간 후, 실패 (오늘 할 일 X)
                TaskCreateInfo("scheduled task9", SCHEDULED, yesterdayEndDateTime, FAIL, yesterday8Pm),
                // [Scheduled Task] 오늘 마감시간 전, 오늘 알림시간 후, 작은 행동 미션 수행 중 이탈 (오늘 할 일 O)
                TaskCreateInfo("scheduled task10", SCHEDULED, todayEndDateTime, WARMING_UP, todayH00M00S01),
                // [Scheduled Task] 내일 마감시간 전, 내일 알림시간 전 등록만 한 상태 (오늘 할 일 X)
                TaskCreateInfo("scheduled task11", SCHEDULED, tomorrowEndDateTime, BEFORE, tomorrowStartTime),
            )

        // save task
        taskCreateInfoList.forEach { taskCreateInfo ->
            val task =
                Fixture.createTask(
                    id = null,
                    name = taskCreateInfo.name,
                    category = taskCreateInfo.category,
                    member = Member.fromEntity(member),
                    dueDatetime = taskCreateInfo.dueDatetime,
                    status = taskCreateInfo.status,
                    triggerActionAlarmTime = taskCreateInfo.triggerActionAlarmTime,
                )
            taskRepository.save(task.toEntity())
        }

        // when
        val tasks = taskRepository.findTodayTasks(checkNotNull(member.id))

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
        val dueDatetime: LocalDateTime,
        val status: TaskStatus,
        val triggerActionAlarmTime: LocalDateTime? = null,
    )
}
