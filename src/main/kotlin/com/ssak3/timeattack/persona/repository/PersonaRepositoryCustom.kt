package com.ssak3.timeattack.persona.repository

import com.ssak3.timeattack.persona.repository.entity.PersonaEntity

interface PersonaRepositoryCustom {
    /**
     * 해당 유저의 모든 페르소나 종류 조회
     * - 최신 추가순 정렬
     */
    fun findAllPersonas(memberId: Long): List<PersonaEntity>
}
