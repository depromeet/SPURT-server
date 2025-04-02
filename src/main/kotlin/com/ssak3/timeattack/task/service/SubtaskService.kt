package com.ssak3.timeattack.task.service

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.task.controller.dto.SubtaskUpsertRequest
import com.ssak3.timeattack.task.domain.Subtask
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.repository.SubtaskRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubtaskService(
    private val subtaskRepository: SubtaskRepository,
    private val taskService: TaskService,
) {
    @Transactional
    fun upsert(subtaskUpsertRequest: SubtaskUpsertRequest): Subtask {
        val task = taskService.getTaskById(subtaskUpsertRequest.taskId)
        val subtask =
            Subtask(
                id = subtaskUpsertRequest.id,
                task = task,
                name = subtaskUpsertRequest.name,
            )
        val entity =
            subtaskRepository.findByIdOrNull(subtask.id)?.let {
                val originalSubtask = Subtask.fromEntity(it)
                originalSubtask.modifyName(subtask.name)
                subtaskRepository.save(originalSubtask.toEntity())
            } ?: subtaskRepository.save(subtask.toEntity())

        return Subtask.fromEntity(entity)
    }

    @Transactional
    fun delete(id: Long) {
        val entity =
            subtaskRepository.findByIdOrNull(id)
                ?: throw ApplicationException(ApplicationExceptionType.SUBTASK_NOT_FOUND_BY_ID, id)
        val subtask = Subtask.fromEntity(entity)
        subtask.delete()
        subtaskRepository.save(subtask.toEntity())
    }

    @Transactional
    fun updateStatus(id: Long): Subtask {
        val entity =
            subtaskRepository.findByIdOrNull(id)
                ?: throw ApplicationException(ApplicationExceptionType.SUBTASK_NOT_FOUND_BY_ID, id)
        val subtask = Subtask.fromEntity(entity)
        subtask.changeStatus()

        val updatedSubtask = subtaskRepository.save(subtask.toEntity())
        return Subtask.fromEntity(updatedSubtask)
    }

    fun getAll(taskId: Long): List<Subtask> {
        val taskEntity = taskService.getTaskById(taskId).toEntity()
        return subtaskRepository.findAllByTaskAndIsDeletedIs(task = taskEntity).map { Subtask.fromEntity(it) }
    }
}
