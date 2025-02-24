package com.ssak3.timeattack.task.repository.entity

import com.ssak3.timeattack.common.domain.BaseEntity
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.persona.repository.entity.PersonaEntity
import com.ssak3.timeattack.task.domain.TaskCategory
import com.ssak3.timeattack.task.domain.TaskStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "task")
class TaskEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    val id: Long? = null,
    @Column(name = "name", length = 100)
    val name: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    val category: TaskCategory,
    @Column(name = "due_datetime")
    val dueDatetime: LocalDateTime,
    @Column(name = "trigger_action", length = 100)
    val triggerAction: String? = null,
    @Column(name = "estimated_time")
    val estimatedTime: Int? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    val status: TaskStatus,
    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: MemberEntity,
    @ManyToOne
    @JoinColumn(name = "persona_id")
    val persona: PersonaEntity,
    // TODO: BaseEntity 수정 후 isDeleted 필드 추가
) : BaseEntity()
