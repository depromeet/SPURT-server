package com.ssak3.timeattack.persona.service

import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.persona.repository.PersonaRepository
import com.ssak3.timeattack.persona.service.dto.PersonaDto
import org.springframework.stereotype.Service

@Service
class PersonaService(
    private val personaRepository: PersonaRepository,
) {
    /**
     * 해당 유저의 모든 페르소나 종류 조회
     * - 최신 추가순 정렬
     * - 중복x
     */
    fun findAllPersonas(memberId: Long): List<PersonaDto> =
        personaRepository.findAllPersonas(memberId).map { persona ->
            val personaId = checkNotNull(persona.id, "PersonaId")
            PersonaDto(
                personaId = personaId,
                personaName = persona.name,
            )
        }
}
