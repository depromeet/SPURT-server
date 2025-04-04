package com.ssak3.timeattack.task.scheduler

import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus.Companion.statusesToFail
import com.ssak3.timeattack.task.repository.TaskRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TaskSchedulerInitializer(
    private val taskRepository: TaskRepository,
    private val overdueTaskStatusUpdateScheduler: OverdueTaskStatusUpdateScheduler,
) : Logger {
    @EventListener(ApplicationReadyEvent::class)
    fun initializeTaskSchedulers() {
        logger.info("애플리케이션 시작 시 상태 수정 작업 스케줄러 초기화 시작")

        // 실패 처리 대상이 되는 작업들 조회
        val todoTaskEntities =
            taskRepository.findTodoTasks(
                statusesToFail,
            )

        logger.info("상태 수정 처리 스케줄러에 등록할 작업 수: ${todoTaskEntities.size}")

        // 각 작업에 대해 스케줄러 등록
        todoTaskEntities.forEach { taskEntity ->
            val task = Task.fromEntity(taskEntity)
            logger.info("작업 스케줄러 등록: Task ID=${task.id}, 상태=${task.status}, 마감시간=${task.dueDatetime}")
            overdueTaskStatusUpdateScheduler.scheduleTaskStatusUpdate(task)
        }

        logger.info("상태 수정 작업 스케줄러 초기화 완료")
    }
}
