package com.ssak3.timeattack.persona.service

import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.repository.PersonaRepository
import org.springframework.stereotype.Service

@Service
class PersonaService(
    private val personaRepository: PersonaRepository,
) {
    /**
     * 특정 회원의 페르소나 목록을 가장 최근에 업데이트된 태스크 기준으로 정렬하여 조회한다.
     *
     * 각 페르소나마다 최대 1개의 결과만 반환하며, 여러 태스크가 있는 경우 최신 업데이트 기준으로 정렬한다.
     * 삭제된 태스크(isDeleted=true)는 제외된다.
     */
    fun getAllPersonas(memberId: Long): List<Persona> =
        personaRepository.findPersonasByMemberIdOrderByLatestTask(memberId).map { Persona.fromEntity(it) }
}
