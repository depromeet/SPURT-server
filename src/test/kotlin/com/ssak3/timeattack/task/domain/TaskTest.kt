package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.task.domain.TaskStatus.BEFORE
import com.ssak3.timeattack.task.domain.TaskStatus.COMPLETE
import com.ssak3.timeattack.task.domain.TaskStatus.FAIL
import com.ssak3.timeattack.task.domain.TaskStatus.FOCUSED
import com.ssak3.timeattack.task.domain.TaskStatus.HOLDING_OFF
import com.ssak3.timeattack.task.domain.TaskStatus.PROCRASTINATING
import com.ssak3.timeattack.task.domain.TaskStatus.WARMING_UP
import com.ssak3.timeattack.utils.listToRowArray
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class TaskTest : DescribeSpec({
    describe("Task 클래스 수정 시") {
        context("Urgent Task의 모든 가능한 Status에서") {
            val statusList = listOf(
                FOCUSED,
                FAIL,
                COMPLETE,
            )
            it("이름 수정이 가능하다.") {
                forAll(*listToRowArray(statusList)) { status ->
                    val task =
                        Fixture.createUrgentTask(
                            status = status,
                        )

                    shouldNotThrowAny {
                        task.modifyName("modified name")
                    }
                }
            }
        }

        context("Scheduled Task의 모든 가능한 Status에서") {
            val statusList = listOf(
                BEFORE,
                WARMING_UP,
                HOLDING_OFF,
                FOCUSED,
                FAIL,
                PROCRASTINATING,
                COMPLETE,
            )
            it("이름 수정이 가능하다.") {
                forAll(*listToRowArray(statusList)) { status ->
                    val task =
                        Fixture.createScheduledTask(
                            status = status,
                        )

                    shouldNotThrowAny {
                        task.modifyName("modified name")
                    }
                }
            }
        }


        context("BEFORE 상태에서") {
            lateinit var task: Task
            lateinit var now: LocalDateTime

            beforeEach {
                now = LocalDateTime.now()
                task = Fixture.createScheduledTask(
                    status = BEFORE,
                    dueDatetime = now.plusHours(2),
                    estimatedTime = 60,
                    triggerActionAlarmTime = now.plusMinutes(30)
                )
            }

            it("작은 행동 수정이 가능하다.") {
                shouldNotThrowAny {
                    task.modifyTriggerAction("modified trigger action")
                }
            }

            it ("유효한 예상 소요 시간 수정이 가능하다.") {
                shouldNotThrowAny {
                    task.modifyEstimatedTime(10, now.plusMinutes(20))
                }
            }
        }

        context("BEFORE가 아닌 상태에서") {
            val statusExceptBefore = listOf(
                WARMING_UP,
                HOLDING_OFF,
                FOCUSED,
                FAIL,
                PROCRASTINATING,
                COMPLETE
            )

            it("작은 행동 수정이 불가능하다.") {
                forAll(*listToRowArray(statusExceptBefore)) { status ->
                    val task = Fixture.createScheduledTask(status = status)
                    shouldThrow<ApplicationException> {
                        task.modifyTriggerAction("modified trigger action")
                    }.apply {
                        this.exceptionType shouldBe ApplicationExceptionType.INVALID_TASK_STATUS_FOR_UPDATE
                    }
                }
            }

            it("유효한 예상 소요 시간이어도 수정이 불가능하다.") {
                forAll(*listToRowArray(statusExceptBefore)) { status ->
                    val task = Fixture.createScheduledTask(status = status)
                    shouldThrow<ApplicationException> {
                        task.modifyEstimatedTime(10, LocalDateTime.now().plusMinutes(20))
                    }.apply {
                        this.exceptionType shouldBe ApplicationExceptionType.INVALID_TASK_STATUS_FOR_UPDATE
                    }
                }
            }
        }


    }
})
