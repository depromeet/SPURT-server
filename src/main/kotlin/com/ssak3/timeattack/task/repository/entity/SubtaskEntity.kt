package com.ssak3.timeattack.task.repository.entity

import com.ssak3.timeattack.common.domain.BaseEntity
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

@Entity
@Table(name = "subtask")
class SubtaskEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "task_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val task: TaskEntity,
    @Column(name = "name", length = 40)
    val name: String,
    @Column(name = "is_deleted")
    val isDeleted: Boolean,
    @Column(name = "is_completed")
    val isCompleted: Boolean,
) : BaseEntity()
