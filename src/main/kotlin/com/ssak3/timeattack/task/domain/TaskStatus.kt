package com.ssak3.timeattack.task.domain

enum class TaskStatus {
    BEFORE,
    WARMING_UP,
    PROCRASTINATING,
    FOCUSED,
    COMPLETE,
    FAIL,
    ;

    fun canTransitionTo(next: TaskStatus): Boolean {
        return when (this) {
            // 항상 다음 상태로만 전환 가능
            BEFORE -> next == WARMING_UP || next == PROCRASTINATING || next == FOCUSED || next == COMPLETE || next == FAIL
            WARMING_UP -> next == FOCUSED || next == COMPLETE || next == FAIL
            PROCRASTINATING -> next == FOCUSED || next == FAIL
            FOCUSED -> next == COMPLETE
            // 최종 상태에서는 더 이상 전환 불가
            COMPLETE, FAIL -> false
        }
    }
}
