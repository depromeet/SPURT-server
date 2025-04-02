package com.ssak3.timeattack.mypage.controller.dto

import com.ssak3.timeattack.persona.service.dto.PersonaDto
import com.ssak3.timeattack.task.controller.dto.TaskResponse

data class MyPageResponse(
    val satisfactionAvg: Int,
    val concentrationAvg: Int,
    val personas: List<PersonaDto>,
    val completedTasks: List<TaskResponse>,
    val procrastinatedTasks: List<TaskResponse>,
    val completedTaskCount: Int,
    val procrastinatedTaskCount: Int,
)
