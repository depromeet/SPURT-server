package com.ssak3.timeattack.mypage.service

import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.mypage.controller.dto.MyPageResponse
import com.ssak3.timeattack.persona.service.PersonaService
import com.ssak3.timeattack.persona.service.dto.PersonaDto
import com.ssak3.timeattack.retrospection.service.RetrospectionService
import com.ssak3.timeattack.task.controller.dto.TaskResponse
import com.ssak3.timeattack.task.service.TaskService
import org.springframework.stereotype.Service

@Service
class MyPageService(
    private val taskService: TaskService,
    private val personaService: PersonaService,
    private val retrospectionService: RetrospectionService,
) : Logger {
    fun myPage(member: Member): MyPageResponse {
        checkNotNull(member.id, "MemberId")
        // 회고 만족도, 집중도 평균 조회
        val (satisfactionAvg, concentrationAvg) = retrospectionService.getRetrospectionAverage(member.id)

        // 역대 페르소나 조회
        val personas = personaService.getAllPersonas(member.id)

        // 완료한 일 목록 조회
        val completedTasks = taskService.getCompletedTasksOrderByCompletedTimeDesc(member.id)

        // 미룬일 목록 조회
        val procrastinatedTasks = taskService.getProcrastinatedTasksOrderByDueDateDesc(member.id)

        // MyPageResponse 반환
        return MyPageResponse(
            satisfactionAvg = satisfactionAvg,
            concentrationAvg = concentrationAvg,
            personas = personas.map { PersonaDto.fromPersona(it) },
            completedTasks = completedTasks.map { TaskResponse.fromTask(it) },
            procrastinatedTasks = procrastinatedTasks.map { TaskResponse.fromTask(it) },
            completedTaskCount = completedTasks.size,
            procrastinatedTaskCount = procrastinatedTasks.size,
        )
    }
}
