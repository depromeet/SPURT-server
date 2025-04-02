package com.ssak3.timeattack.persona.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.persona.repository.entity.QPersonaEntity
import com.ssak3.timeattack.task.repository.entity.QTaskEntity
import org.springframework.stereotype.Repository

@Repository
class PersonaRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : PersonaRepositoryCustom {
    private val qPersona: QPersonaEntity = QPersonaEntity.personaEntity
    private val qTask: QTaskEntity = QTaskEntity.taskEntity

    override fun findAllPersonas(memberId: Long): List<PersonaEntity> {
        return queryFactory
            .select(qPersona)
            .from(qPersona)
            .join(qTask).on(qTask.persona.eq(qPersona).and(qTask.member.id.eq(memberId)))
            .where(qTask.isDeleted.isFalse)
            .groupBy(qPersona.id, qPersona.name)
            .orderBy(qTask.updatedAt.max().desc())
            .fetch()
    }
}
