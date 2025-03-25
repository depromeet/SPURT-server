package com.ssak3.timeattack.task.scheduler

import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.domain.TaskStatus.Companion.statusesToFail
import com.ssak3.timeattack.task.repository.TaskRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class TaskSchedulerInitializerTest : BehaviorSpec({
    // 테스트 대상 객체와 의존성
    val taskRepository = mockk<TaskRepository>()
    val overdueTaskFailureScheduler = mockk<OverdueTaskFailureScheduler>()
    val taskSchedulerInitializer = TaskSchedulerInitializer(taskRepository, overdueTaskFailureScheduler)

    // 테스트 데이터 준비
    val now = LocalDateTime.now()
    val tomorrow = now.plusDays(1)
    val dayAfterTomorrow = now.plusDays(2)

    // Fixture를 활용한 테스트 데이터 생성
    val task1 =
        Fixture.createScheduledTask(
            id = 1L,
            status = TaskStatus.BEFORE,
            dueDatetime = tomorrow,
        )

    val task2 =
        Fixture.createScheduledTask(
            id = 2L,
            status = TaskStatus.PROCRASTINATING,
            dueDatetime = tomorrow,
        )

    val task3 =
        Fixture.createScheduledTask(
            id = 3L,
            status = TaskStatus.HOLDING_OFF,
            dueDatetime = dayAfterTomorrow,
        )

    // 테스트용 TaskEntity 생성
    val taskEntities =
        listOf(
            task1.toEntity(),
            task2.toEntity(),
            task3.toEntity(),
        )

    Given("실패 처리 대상이 되는 작업들이 있을 때") {
        // 이전 테스트의 모킹 초기화
        clearMocks(overdueTaskFailureScheduler, taskRepository)

        // 리포지토리 모킹 설정
        every {
            taskRepository.findTodoTasks(statusesToFail)
        } returns taskEntities

        // 스케줄러 모킹 설정
        every {
            overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(any())
        } just Runs

        When("initializeTaskSchedulers가 호출되면") {
            taskSchedulerInitializer.initializeTaskSchedulers()

            Then("모든 대상 작업에 대해 스케줄러가 등록되어야 한다") {
                // 등록된 작업 수 검증
                verify(exactly = 3) {
                    overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(any())
                }

                // ID로 작업 검증
                verify {
                    overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(match { it.id == 1L })
                    overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(match { it.id == 2L })
                    overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(match { it.id == 3L })
                }
            }
        }
    }

    Given("실패 처리 대상 작업이 없을 때") {
        // 이전 테스트의 모킹 초기화
        clearMocks(overdueTaskFailureScheduler, taskRepository)

        // 리포지토리 모킹 설정 - 빈 리스트 반환
        every {
            taskRepository.findTodoTasks(statusesToFail)
        } returns emptyList()

        When("initializeTaskSchedulers가 호출되면") {
            taskSchedulerInitializer.initializeTaskSchedulers()

            Then("스케줄러 등록이 호출되지 않아야 한다") {
                // 스케줄러 등록 호출 안 됨 검증
                verify(exactly = 0) {
                    overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(any())
                }
            }
        }
    }
})
