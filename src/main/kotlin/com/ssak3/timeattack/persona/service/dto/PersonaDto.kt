package com.ssak3.timeattack.persona.service.dto

import com.ssak3.timeattack.persona.domain.Persona

data class PersonaDto(
    val personaId: Long,
    val personaName: String,
) {
    companion object {
        fun fromPersona(persona: Persona) =
            PersonaDto(
                personaId = persona.id,
                personaName = persona.name,
            )
    }
}
