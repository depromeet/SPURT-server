package com.ssak3.timeattack.task.scheduler

import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.domain.TaskStatus.Companion.statusesToFail
import com.ssak3.timeattack.task.repository.TaskRepository
import com.ssak3.timeattack.task.repository.entity.TaskEntity
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.LocalDateTime

// @IntegrationTest
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

    // 테스트용 TaskEntity 모킹
    val taskEntity1 = mockk<TaskEntity>()
    val taskEntity2 = mockk<TaskEntity>()
    val taskEntity3 = mockk<TaskEntity>()

    // Task.fromEntity 모킹
    mockkObject(Task.Companion)
    every { Task.fromEntity(taskEntity1) } returns task1
    every { Task.fromEntity(taskEntity2) } returns task2
    every { Task.fromEntity(taskEntity3) } returns task3

    Given("실패 처리 대상이 되는 작업들이 있을 때") {
        // 이전 테스트의 모킹 초기화
        clearMocks(overdueTaskFailureScheduler, taskRepository)

        // 리포지토리 모킹 설정
        every {
            taskRepository.findTodoTasks(statusesToFail)
        } returns listOf(taskEntity1, taskEntity2, taskEntity3)

        // 스케줄러 모킹 설정
        every {
            overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(any())
        } just Runs

        When("initializeTaskSchedulers가 호출되면") {
            taskSchedulerInitializer.initializeTaskSchedulers()

            Then("모든 대상 작업에 대해 스케줄러가 등록되어야 한다") {
                // 메서드 호출 횟수 검증
                verify(exactly = 1) {
                    taskRepository.findTodoTasks(statusesToFail)
                }

                // 각 작업에 대해 스케줄러 등록 검증
                verify(exactly = 1) {
                    overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(task1)
                    overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(task2)
                    overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(task3)
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

        When("애플리케이션이 시작되면") {
            taskSchedulerInitializer.initializeTaskSchedulers()

            Then("스케줄러 등록이 호출되지 않아야 한다") {
                // 메서드 호출 검증
                verify(exactly = 1) {
                    taskRepository.findTodoTasks(statusesToFail)
                }

                // 스케줄러 등록 호출 안 됨 검증
                verify(exactly = 0) {
                    overdueTaskFailureScheduler.scheduleTaskTimeoutFailure(any())
                }
            }
        }
    }

    // 테스트 종료 후 모킹 해제
    afterSpec {
        unmockkAll()
    }
})
