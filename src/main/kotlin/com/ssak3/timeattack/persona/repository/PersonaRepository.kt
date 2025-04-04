package com.ssak3.timeattack.persona.repository

import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PersonaRepository : JpaRepository<PersonaEntity, Long>, PersonaRepositoryCustom {
    fun findByTaskTypeAndTaskMode(
        taskType: TaskTypeEntity,
        taskMode: TaskModeEntity,
    ): PersonaEntity?
}
