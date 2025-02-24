package com.ssak3.timeattack.persona.repository

import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PersonaSpringDataRepository : JpaRepository<PersonaEntity, Long> {
    fun findByTaskTypeEntityAndTaskModeEntity(
        taskTypeEntity: TaskTypeEntity,
        taskModeEntity: TaskModeEntity,
    ): PersonaEntity?
}
