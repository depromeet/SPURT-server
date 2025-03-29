package com.ssak3.timeattack.retrospection.repository.entity

import com.ssak3.timeattack.common.domain.BaseEntity
import com.ssak3.timeattack.member.repository.entity.MemberEntity
import com.ssak3.timeattack.task.repository.entity.TaskEntity
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
@Table(name = "retrospection")
class RetrospectionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val member: MemberEntity,
    @ManyToOne
    @JoinColumn(name = "task_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val task: TaskEntity,
    @Column(name = "satisfaction")
    val satisfaction: Int,
    @Column(name = "concentration")
    val concentration: Int,
    @Column(name = "comment", length = 100)
    val comment: String? = null,
) : BaseEntity()
