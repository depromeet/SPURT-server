package com.ssak3.timeattack.task.domain

import com.ssak3.timeattack.task.domain.TaskStatus.BEFORE
import com.ssak3.timeattack.task.domain.TaskStatus.COMPLETE
import com.ssak3.timeattack.task.domain.TaskStatus.FAIL
import com.ssak3.timeattack.task.domain.TaskStatus.FOCUSED
import com.ssak3.timeattack.task.domain.TaskStatus.HOLDING_OFF
import com.ssak3.timeattack.task.domain.TaskStatus.PROCRASTINATING
import com.ssak3.timeattack.task.domain.TaskStatus.WARMING_UP
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class TaskStatusTest : DescribeSpec({
    describe("canTransitionTo 메서드는") {
        context("현재 상태에서 이후 상태로의 전환을 허용한다") {
            forAll(
                row(BEFORE, WARMING_UP),
                row(BEFORE, HOLDING_OFF),
                row(BEFORE, FOCUSED),
                row(BEFORE, FAIL),
                row(WARMING_UP, FOCUSED),
                row(WARMING_UP, FAIL),
                row(HOLDING_OFF, FOCUSED),
                row(HOLDING_OFF, PROCRASTINATING),
                row(HOLDING_OFF, FAIL),
                row(PROCRASTINATING, FOCUSED),
                row(PROCRASTINATING, FAIL),
                row(FOCUSED, COMPLETE),
            ) { initialStatus, newStatus ->
                it("$initialStatus -> $newStatus 전환은 허용된다") {
                    initialStatus.canTransitionTo(newStatus) shouldBe true
                }
            }
        }

        context("유효하지 않은 상태 전환 시 false를 반환한다") {
            forAll(
                row(COMPLETE, BEFORE),
                row(COMPLETE, WARMING_UP),
                row(COMPLETE, PROCRASTINATING),
                row(COMPLETE, HOLDING_OFF),
                row(COMPLETE, FOCUSED),
                row(COMPLETE, FAIL),
                row(FAIL, BEFORE),
                row(FAIL, WARMING_UP),
                row(FAIL, PROCRASTINATING),
                row(FAIL, HOLDING_OFF),
                row(FAIL, FOCUSED),
                row(FAIL, COMPLETE),
                row(FOCUSED, BEFORE),
                row(FOCUSED, WARMING_UP),
                row(FOCUSED, PROCRASTINATING),
                row(FOCUSED, HOLDING_OFF),
                row(FOCUSED, FAIL),
                row(PROCRASTINATING, BEFORE),
                row(PROCRASTINATING, WARMING_UP),
                row(PROCRASTINATING, HOLDING_OFF),
                row(PROCRASTINATING, COMPLETE),
                row(HOLDING_OFF, BEFORE),
                row(HOLDING_OFF, WARMING_UP),
                row(HOLDING_OFF, COMPLETE),
                row(WARMING_UP, BEFORE),
                row(WARMING_UP, HOLDING_OFF),
                row(WARMING_UP, PROCRASTINATING),
                row(WARMING_UP, COMPLETE),
                row(BEFORE, PROCRASTINATING),
                row(BEFORE, COMPLETE),
            ) { initialStatus, newStatus ->
                it("$initialStatus -> $newStatus 전환은 허용되지 않는다") {
                    initialStatus.canTransitionTo(newStatus) shouldBe false
                }
            }
        }
    }
})
