package com.ssak3.timeattack.persona.repository.entity

import com.ssak3.timeattack.persona.domain.Persona
import com.ssak3.timeattack.persona.domain.TaskKeywordsCombination
import com.ssak3.timeattack.task.domain.TaskMode
import com.ssak3.timeattack.task.domain.TaskType
import com.ssak3.timeattack.task.repository.entity.TaskModeEntity
import com.ssak3.timeattack.task.repository.entity.TaskTypeEntity
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "persona",
    uniqueConstraints = [
        UniqueConstraint(name = "UK_TASK_TYPE__TASK_MODE", columnNames = ["task_type_id", "task_mode_id"]),
    ],
)
class PersonaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id")
    val id: Long? = null,
    @Column(name = "name", unique = true, length = 100)
    val name: String,
    @Column(name = "persona_image_url", length = 255)
    val personaImageUrl: String,
    @ManyToOne
    @JoinColumn(name = "task_type_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val taskTypeEntity: TaskTypeEntity,
    @ManyToOne
    @JoinColumn(name = "task_mode_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val taskModeEntity: TaskModeEntity,
)
