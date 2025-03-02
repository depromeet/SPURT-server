package com.ssak3.timeattack.task.domain

enum class TaskStatus {
    BEFORE,
    WARMING_UP,
    PROCRASTINATING,
    HOLDING_OFF,
    FOCUSED,
    COMPLETE,
    FAIL,
    ;

    fun canTransitionTo(next: TaskStatus): Boolean {
        return when (this) {
            // 항상 다음 상태로만 전환 가능
            BEFORE ->
                next == WARMING_UP || next == HOLDING_OFF || next == FOCUSED || next == FAIL
            WARMING_UP -> next == FOCUSED || next == FAIL
            HOLDING_OFF -> next == FOCUSED || next == PROCRASTINATING || next == FAIL
            PROCRASTINATING -> next == FOCUSED || next == FAIL
            // TODO: FOCUSED에서 마감시간 지났을 때 FAIL로 처리할 지 고민
            FOCUSED -> next == COMPLETE
            // 최종 상태에서는 더 이상 전환 불가
            COMPLETE, FAIL -> false
        }
    }
}
