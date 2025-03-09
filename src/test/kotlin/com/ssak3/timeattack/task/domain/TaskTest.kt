package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.fixture.Fixture
import com.ssak3.timeattack.task.domain.TaskStatus.BEFORE
import com.ssak3.timeattack.task.domain.TaskStatus.COMPLETE
import com.ssak3.timeattack.task.domain.TaskStatus.FAIL
import com.ssak3.timeattack.task.domain.TaskStatus.FOCUSED
import com.ssak3.timeattack.task.domain.TaskStatus.HOLDING_OFF
import com.ssak3.timeattack.task.domain.TaskStatus.PROCRASTINATING
import com.ssak3.timeattack.task.domain.TaskStatus.WARMING_UP
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.data.row

class TaskTest : DescribeSpec({
    describe("Task 클래스 수정 시") {
        context("Urgent Task의 모든 가능한 Status에서") {
            forAll(
                row(FOCUSED),
                row(FAIL),
                row(COMPLETE),
            ) { status ->
                it("이름 수정이 가능하다. (status : $status)") {
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
            forAll(
                row(BEFORE),
                row(WARMING_UP),
                row(HOLDING_OFF),
                row(FOCUSED),
                row(FAIL),
                row(PROCRASTINATING),
                row(COMPLETE),
            ) { status ->
                it("이름 수정이 가능하다. (status : $status)") {
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
    }
})
