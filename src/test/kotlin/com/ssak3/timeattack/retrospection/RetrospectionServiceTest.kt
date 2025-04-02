package com.ssak3.timeattack.retrospection

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.retrospection.controller.dto.RetrospectionCreateRequest
import com.ssak3.timeattack.retrospection.repository.RetrospectionRepository
import com.ssak3.timeattack.retrospection.repository.entity.RetrospectionEntity
import com.ssak3.timeattack.retrospection.service.RetrospectionService
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.service.TaskService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class RetrospectionServiceTest : BehaviorSpec({
    // 공통 객체 설정
    val retrospectionRepository = mockk<RetrospectionRepository>()
    val taskService = mockk<TaskService>()
    val retrospectionService = RetrospectionService(retrospectionRepository, taskService)
    val member = Fixture.createMember()

    given("회고를 생성할 때") {
        // 테스트에 필요한 데이터 준비
        val completedTask = Fixture.createTask(member = member, status = TaskStatus.COMPLETE)
        val notCompletedTask = Fixture.createTask(id = 2L, member = member, status = TaskStatus.BEFORE)
        val focusedTask = Fixture.createTask(id = 3L, member = member, status = TaskStatus.FOCUSED)
        val taskId = 1L
        val notCompletedTaskId = 2L
        val focusedTaskId = 3L

        // 모킹 설정
        every { taskService.findTaskByIdAndMember(member, taskId) } returns completedTask
        every { taskService.findTaskByIdAndMember(member, notCompletedTaskId) } returns notCompletedTask
        every { taskService.findTaskByIdAndMember(member, focusedTaskId) } returns focusedTask
        every {
            taskService.changeTaskStatus(
                focusedTaskId,
                checkNotNull(member.id),
                TaskStatus.COMPLETE,
            )
        } returns mockk()
        every { retrospectionRepository.save(any<RetrospectionEntity>()) } returns mockk()

        `when`("완료된 작업에 대한 유효한 요청이 주어지면") {
            val request =
                RetrospectionCreateRequest(
                    satisfaction = 40,
                    concentration = 60,
                    comment = "작업을 효율적으로 완료했습니다.",
                )

            retrospectionService.createRetrospection(request, member, taskId)

            then("회고가 저장된다") {
                verify { retrospectionRepository.save(any<RetrospectionEntity>()) }
            }
        }

        `when`("몰입중인 작업에 대한 유효한 요청이 주어지면") {
            val request =
                RetrospectionCreateRequest(
                    satisfaction = 40,
                    concentration = 60,
                    comment = "작업을 효율적으로 완료했습니다.",
                )

            retrospectionService.createRetrospection(request, member, focusedTaskId)

            then("회고가 저장되고 작업 상태가 완료로 변경된다") {
                verify { retrospectionRepository.save(any<RetrospectionEntity>()) }
                val memberId = checkNotNull(member.id, "MemberId")
                verify { taskService.changeTaskStatus(focusedTaskId, memberId, TaskStatus.COMPLETE) }
            }
        }

        `when`("완료되지 않은 작업에 대한 요청이 주어지면") {
            val request =
                RetrospectionCreateRequest(
                    satisfaction = 40,
                    concentration = 60,
                    comment = "작업을 효율적으로 완료했습니다.",
                )

            then("예외가 발생한다") {
                shouldThrow<ApplicationException> {
                    retrospectionService.createRetrospection(request, member, notCompletedTaskId)
                }
            }
        }

        `when`("comment가 null인 경우") {
            val request =
                RetrospectionCreateRequest(
                    satisfaction = 50,
                    concentration = 50,
                    comment = null,
                )

            retrospectionService.createRetrospection(request, member, taskId)

            then("comment가 null인 회고가 저장된다") {
                verify {
                    retrospectionRepository.save(
                        match<RetrospectionEntity> {
                            it.comment == null
                        },
                    )
                }
            }
        }
    }

    given("회고 평균 조회 시") {
        val memberId = 1L

        `when`("회고가 있는 경우") {
            val mockRetrospectives =
                listOf(
                    mockk<RetrospectionEntity>().apply {
                        every { satisfaction } returns 40
                        every { concentration } returns 60
                    },
                    mockk<RetrospectionEntity>().apply {
                        every { satisfaction } returns 80
                        every { concentration } returns 20
                    },
                )

            every { retrospectionRepository.findAllByMemberId(memberId) } returns mockRetrospectives

            then("만족도와 집중도의 평균이 올바르게 계산된다") {
                val (satisfactionAvg, concentrationAvg) = retrospectionService.getRetrospectionAverage(memberId)
                satisfactionAvg shouldBe 60
                concentrationAvg shouldBe 40
            }
        }

        `when`("회고가 없는 경우") {
            every { retrospectionRepository.findAllByMemberId(memberId) } returns emptyList()

            then("만족도와 집중도 평균은 0이 된다") {
                val (satisfactionAvg, concentrationAvg) = retrospectionService.getRetrospectionAverage(memberId)
                satisfactionAvg shouldBe 0
                concentrationAvg shouldBe 0
            }
        }
    }
})
