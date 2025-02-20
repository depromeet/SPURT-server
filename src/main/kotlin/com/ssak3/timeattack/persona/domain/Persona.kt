package com.ssak3.timeattack.persona.domain

class Persona(
    val id: Long,
    val name: String,
    val personaImageUrl: String,
    val taskKeywordsCombination: TaskKeywordsCombination,
)
