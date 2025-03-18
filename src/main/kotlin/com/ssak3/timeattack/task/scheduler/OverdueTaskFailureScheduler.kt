package com.ssak3.timeattack.task.scheduler

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.repository.TaskRepository
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.time.ZoneId

@Service
class OverdueTaskFailureScheduler(
    private val taskRepository: TaskRepository,
    private val taskScheduler: TaskScheduler,
    private val transactionTemplate: TransactionTemplate,
) : Logger {
    fun scheduleTaskTimeoutFailure(task: Task) {
        checkNotNull(task.id, "taskId")
        taskScheduler.schedule(
            { checkAndUpdateTaskStatus(task.id) },
            task.dueDatetime.plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant(),
        )
    }

    /**
     * task 상태가 BEFORE, PROCRASTINATING, HOLDING_OFF, WARMING_UP 이라면 Fail 처리
     */
    private fun checkAndUpdateTaskStatus(taskId: Long) {
        logger.info("Task 마감 체크 스케줄러 스레드 동작 시작! ${Thread.currentThread().name}")
        transactionTemplate.execute {
            val task =
                taskRepository.findByIdAndIsDeletedIsFalse(taskId)
                    ?.let { Task.fromEntity(it) }
                    ?: throw ApplicationException(
                        ApplicationExceptionType.TASK_NOT_FOUND_BY_ID,
                        taskId,
                    )

            if (task.status in statusesToFail) {
                logger.info("현재 상태가 ${task.status}인 Task(${task.id})는 마감 시간이 지나 Fail 처리 됩니다.")
                task.status = TaskStatus.FAIL
                taskRepository.save(task.toEntity())
            }
        }
    }

    companion object {
        val statusesToFail =
            listOf(
                TaskStatus.BEFORE,
                TaskStatus.PROCRASTINATING,
                TaskStatus.HOLDING_OFF,
                TaskStatus.WARMING_UP,
            )
    }
}
