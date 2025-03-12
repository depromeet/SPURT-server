package com.ssak3.timeattack.task.controller.dto

import com.ssak3.timeattack.task.domain.Task

// TODO 응답 체크
data class HomeTasksResponse(
    val todayTasks: List<TaskResponse>,
    val weeklyTasks: List<TaskResponse>,
    val allTasks: List<TaskResponse>,
    val missionEscapeTask: TaskResponse?,
) {
    companion object {
        fun fromTasks(
            todayTasks: List<Task>,
            weeklyTasks: List<Task>,
            allTasks: List<Task>,
            missionEscapeTask: Task?,
        ): HomeTasksResponse {
            return HomeTasksResponse(
                todayTasks = todayTasks.map { TaskResponse.fromTask(it) },
                weeklyTasks = weeklyTasks.map { TaskResponse.fromTask(it) },
                allTasks = allTasks.map { TaskResponse.fromTask(it) },
                missionEscapeTask = missionEscapeTask?.let { TaskResponse.fromTask(it) },
            )
        }
    }
}
