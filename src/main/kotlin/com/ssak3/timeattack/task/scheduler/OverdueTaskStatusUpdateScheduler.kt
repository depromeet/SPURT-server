package com.ssak3.timeattack.task.scheduler

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.external.firebase.domain.DevicePlatform
import com.ssak3.timeattack.notifications.domain.FcmMessage
import com.ssak3.timeattack.notifications.service.FcmDeviceService
import com.ssak3.timeattack.notifications.service.FcmPushNotificationService
import com.ssak3.timeattack.retrospection.repository.RetrospectionRepository
import com.ssak3.timeattack.task.domain.Task
import com.ssak3.timeattack.task.domain.TaskStatus
import com.ssak3.timeattack.task.domain.TaskStatus.Companion.statusesToFail
import com.ssak3.timeattack.task.domain.TaskStatus.FAIL
import com.ssak3.timeattack.task.domain.TaskStatus.FOCUSED
import com.ssak3.timeattack.task.repository.TaskRepository
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.time.ZoneId

@Service
class OverdueTaskStatusUpdateScheduler(
    private val taskRepository: TaskRepository,
    private val retrospectionRepository: RetrospectionRepository,
    private val fcmPushNotificationService: FcmPushNotificationService,
    private val fcmDeviceService: FcmDeviceService,
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

        val scheduledTimeForPushNotification = task.dueDatetime.plusMinutes(30)
        taskScheduler.schedule(
            { checkRetrospectionAndSendPushNotification(task.id) },
            scheduledTimeForPushNotification.atZone(ZoneId.systemDefault()).toInstant(),
        )

        logger.info("Task(${task.id}) 회고 푸시 알림 등록 완료: 예정 실행 시간 = $scheduledTime")
    }

    private fun checkRetrospectionAndSendPushNotification(taskId: Long) {
        val isExist = retrospectionRepository.findByTaskId(taskId) != null
        val task =
            taskRepository.findByIdOrNull(taskId)
                ?: throw ApplicationException(ApplicationExceptionType.TASK_NOT_FOUND_BY_ID, taskId)
        val memberId = checkNotNull(task.member.id, "memberId")

        if (!isExist) {
            fcmDeviceService.getDevicesByMember(memberId).forEach { device ->
                val message =
                    FcmMessage(
                        token = device.fcmRegistrationToken,
                        platform = DevicePlatform.valueOf(device.devicePlatform.toString()),
                        taskId = checkNotNull(task.id, "task id"),
                        body =
                            """
                            ${task.name} 마감일이 끝났어요!
                            회고를 작성하며 과정을 돌아보세요.
                            """.trimIndent(),
                        route = "/retrospection",
                    )

                fcmPushNotificationService.sendNotification(message)
            }
        }
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
