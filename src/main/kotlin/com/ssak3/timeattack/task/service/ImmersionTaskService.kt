package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.task.domain.ImmersionTask
import org.springframework.stereotype.Service

@Service
class ImmersionTaskService(
    private val taskService: TaskService,
    private val subtaskService: SubtaskService,
) {
    fun getImmersionTasks(member: Member): List<ImmersionTask> {
        val activeTasks = taskService.getActiveTasks(member)
        val immersionTasks: List<ImmersionTask> =
            activeTasks.map { task ->
                checkNotNull(task.id, "taskId")
                val subtask = subtaskService.getAll(task)
                ImmersionTask(
                    taskId = task.id,
                    taskName = task.name,
                    dueDatetime = task.dueDatetime,
                    personaId = task.persona.id,
                    personaName = task.persona.name,
                    subtasks = subtask,
                )
            }

        return immersionTasks
    }
}
