package com.ssak3.timeattack.task.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "task_mode")
class TaskModeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_mode_id")
    val id: Int? = null,
    @Column(name = "name", unique = true, length = 20)
    val name: String,
)
