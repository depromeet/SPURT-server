package com.ssak3.timeattack.persona.domain

import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.domain.TaskMode
import com.ssak3.timeattack.task.domain.TaskType

class Persona(
    val id: Long,
    val name: String,
    val personaImageUrl: String,
    val taskKeywordsCombination: TaskKeywordsCombination,
) {
    fun toEntity() =
        PersonaEntity(
            id = id,
            name = name,
            personaImageUrl = personaImageUrl,
            taskTypeEntity = taskKeywordsCombination.taskType.toEntity(),
            taskModeEntity = taskKeywordsCombination.taskMode.toEntity(),
        )

    companion object {
        fun fromEntity(entity: PersonaEntity) =
            Persona(
                id = entity.id ?: throw IllegalStateException("id must not be null"),
                name = entity.name,
                personaImageUrl = entity.personaImageUrl,
                taskKeywordsCombination =
                    TaskKeywordsCombination(
                        taskType = TaskType.fromEntity(entity.taskTypeEntity),
                        taskMode = TaskMode.fromEntity(entity.taskModeEntity),
                    ),
            )
    }
}
