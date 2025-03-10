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
    describe("Scheduled Task 생성 시") {
        context("BEFORE 상태로 생성될 때") {
            it("작은 행동 알림으로부터 마감시간까지 남은 시간이 예상 소요시간 보다 적게 생성 될 수 없다.") {
                val now = LocalDateTime.now()
                shouldThrow<ApplicationException> {
                    Task(
                        name = "test task",
                        category = TaskCategory.SCHEDULED,
                        status = BEFORE,
                        triggerActionAlarmTime = now.plusMinutes(20),
                        dueDatetime = now.plusMinutes(30),
                        estimatedTime = 60,
                        triggerAction = "test trigger action",
                        member = Fixture.createMember(),
                        persona = Fixture.createPersona(),
                    )
                }.apply {
                    this.exceptionType shouldBe ApplicationExceptionType.INVALID_TRIGGER_ACTION_ALARM_TIME
                }
            }

            it("작은 행동 알림으로부터 마감시간까지 남은 시간이 예상 소요시간과 같다면 생성될 수 있다.") {
                val now = LocalDateTime.now()
                shouldNotThrowAny {
                    Task(
                        name = "test task",
                        category = TaskCategory.SCHEDULED,
                        status = BEFORE,
                        triggerActionAlarmTime = now.plusMinutes(20),
                        dueDatetime = now.plusMinutes(80),
                        estimatedTime = 60,
                        triggerAction = "test trigger action",
                        member = Fixture.createMember(),
                        persona = Fixture.createPersona(),
                    )
                }
            }

            it("작은 행동 알림으로부터 마감시간까지 남은 시간이 예상 소요시간보다 많다면 생성 될 수 있다.") {
                val now = LocalDateTime.now()
                shouldNotThrowAny {
                    Task(
                        name = "test task",
                        category = TaskCategory.SCHEDULED,
                        status = BEFORE,
                        triggerActionAlarmTime = now.plusMinutes(20),
                        dueDatetime = now.plusMinutes(90),
                        estimatedTime = 60,
                        triggerAction = "test trigger action",
                        member = Fixture.createMember(),
                        persona = Fixture.createPersona(),
                    )
                }
            }
        }
    }

    describe("Task 클래스 수정 시") {
        lateinit var now: LocalDateTime

        beforeEach {
            now = LocalDateTime.now()
        }
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

            beforeEach {
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

            it ("유효한 마감 시간 수정이 가능하다.") {
                shouldNotThrowAny {
                    task.modifyDueDatetime(now.plusMinutes(90), now.plusMinutes(20))
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
                        task.modifyEstimatedTime(10, now.plusMinutes(20))
                    }.apply {
                        this.exceptionType shouldBe ApplicationExceptionType.INVALID_TASK_STATUS_FOR_UPDATE
                    }
                }
            }

            it("유효한 마감 시간이어도 수정이 불가능하다.") {
                forAll(*listToRowArray(statusExceptBefore)) { status ->
                    val task = Fixture.createScheduledTask(status = status)
                    shouldThrow<ApplicationException> {
                        task.modifyDueDatetime(now.plusMinutes(90), now.plusMinutes(20))
                    }.apply {
                        this.exceptionType shouldBe ApplicationExceptionType.INVALID_TASK_STATUS_FOR_UPDATE
                    }
                }
            }
        }

        context("예상 소요 시간이 늘어나는 경우") {
            val task = Fixture.createScheduledTaskWithNow(now)

            it("작은 행동 알림 시간부터 기존 마감 시간까지의 시간보다 커지면 예외가 발생한다.") {
                shouldThrow<ApplicationException> {
                    task.modifyEstimatedTime(180, now.plusMinutes(60))
                }.apply {
                    this.exceptionType shouldBe ApplicationExceptionType.INVALID_TRIGGER_ACTION_ALARM_TIME
                }
            }
        }

        context("마감 시간이 줄고, 즉시 몰입 중으로 바뀌지 않는다면") {
            val task = Fixture.createScheduledTaskWithNow(now)

            it("작은 행동 알림 시간에서 기존 예상 소요시간을 더했을 때 마감 시간보다 크다면 예외가 발생한다.") {
                shouldThrow<ApplicationException> {
                    task.modifyDueDatetime(now.plusMinutes(90), now.plusMinutes(70))
                }.apply {
                    this.exceptionType shouldBe ApplicationExceptionType.INVALID_TRIGGER_ACTION_ALARM_TIME
                }
            }
        }

        context("마감 시간이 줄고, 즉시 몰입 중으로 바뀐다면") {
            lateinit var task: Task

            beforeEach {
                task = Fixture.createScheduledTaskWithNow(now)
            }

            it("상태가 FOCUSED로 바뀐다.") {
                task.modifyToUrgentDueDatetime(now.plusMinutes(30))
                task.status shouldBe FOCUSED
            }

            it("작은 행동 알림 시간이 없어진다.") {
                task.modifyToUrgentDueDatetime(now.plusMinutes(30))
                task.triggerActionAlarmTime shouldBe null
            }
        }
    }
})
