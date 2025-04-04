package com.ssak3.timeattack.mypage.service.dto

import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.task.domain.Task

data class MyPageDto(
    val satisfactionAvg: Int,
    val concentrationAvg: Int,
    val personas: List<Persona>,
    val completedTasks: List<Task>,
    val procrastinatedTasks: List<Task>,
)
