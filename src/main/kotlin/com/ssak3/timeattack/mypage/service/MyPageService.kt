package com.ssak3.timeattack.mypage.service

import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.mypage.service.dto.MyPageDto
import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.retrospection.repository.RetrospectionRepository
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.repository.TaskRepository
import org.springframework.stereotype.Service

@Service
class MyPageService(
    private val taskRepository: TaskRepository,
    private val personaRepository: PersonaRepository,
    private val retrospectionRepository: RetrospectionRepository,
) : Logger {
    fun getMyPage(member: Member): MyPageDto {
        checkNotNull(member.id, "MemberId")
        // 회고 만족도, 집중도 평균 조회
        val retrospectives = retrospectionRepository.findAllByMemberId(member.id)
        val satisfactionAverage = retrospectives.map { it.satisfaction }.average().toInt()
        val concentrationAverage = retrospectives.map { it.concentration }.average().toInt()

        // 역대 페르소나 조회
        val personas =
            personaRepository.findPersonasByMemberIdOrderByLatestTask(
                member.id,
            ).map { Persona.fromEntity(it) }

        // 완료한 일 목록 조회
        val completedTasks =
            taskRepository.findCompletedTasksOrderByCompletedTimeDesc(
                member.id,
            ).map { Task.fromEntity(it) }

        // 미룬일 목록 조회
        val procrastinatedTasks =
            taskRepository.findProcrastinatedTasksOrderByDueDateDesc(member.id).map {
                Task.fromEntity(it)
            }

        return MyPageDto(
            satisfactionAvg = satisfactionAverage,
            concentrationAvg = concentrationAverage,
            personas = personas,
            completedTasks = completedTasks,
            procrastinatedTasks = procrastinatedTasks,
        )
    }
}
