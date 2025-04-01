package com.ssak3.timeattack.task.scheduler

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.domain.TaskStatus.Companion.statusesToFail
import com.ssak3.timeattack.task.domain.TaskStatus.FAIL
import com.ssak3.timeattack.task.domain.TaskStatus.FOCUSED
import com.ssak3.timeattack.task.repository.TaskRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.time.ZoneId

@Service
class OverdueTaskStatusUpdateScheduler(
    private val taskRepository: TaskRepository,
    private val taskScheduler: TaskScheduler,
    private val transactionTemplate: TransactionTemplate,
) : Logger {
    @EventListener
    fun scheduleTaskStatusUpdate(task: Task) {
        checkNotNull(task.id, "taskId")

        val scheduledTime = task.dueDatetime.plusMinutes(1)

        taskScheduler.schedule(
            { checkAndUpdateTaskStatus(task.id) },
            scheduledTime.atZone(ZoneId.systemDefault()).toInstant(),
        )

        logger.info("Task(${task.id}) 상태 체크 스케줄러 등록 완료: 예정 실행 시간 = $scheduledTime")
    }

    /**
     * task 상태가 BEFORE, PROCRASTINATING, HOLDING_OFF, WARMING_UP 이라면 Fail 처리
     * task 상태가 FOCUSED 라면 COMPLETE 처리
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

            val beforeStatus = task.status

            // 상태에 따라 처리
            task.status =
                when (task.status) {
                    in statusesToFail -> FAIL
                    FOCUSED -> TaskStatus.COMPLETE
                    else -> task.status
                }

            // 상태가 변경되었을 때만 저장
            if (task.status != beforeStatus) {
                taskRepository.save(task.toEntity())
                logger.info("현재 상태가 ${beforeStatus}인 Task(${task.id})는 마감 시간이 지나 ${task.status} 처리되었습니다.")
            }
        }
    }
}
