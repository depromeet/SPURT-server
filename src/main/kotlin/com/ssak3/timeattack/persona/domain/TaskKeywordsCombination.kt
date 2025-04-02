package com.ssak3.timeattack.persona.domain

import com.ssak3.timeattack.task.domain.TaskMode
import com.ssak3.timeattack.task.domain.TaskType

class TaskKeywordsCombination(
    val taskType: TaskType,
    val taskMode: TaskMode,
) {
    val keyword = "${this.taskType.name} ${this.taskMode.name}"
}
