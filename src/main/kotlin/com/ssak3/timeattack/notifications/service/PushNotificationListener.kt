package com.ssak3.timeattack.notifications.service

import com.google.cloud.firestore.telemetry.MetricsUtil.logger
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.member.service.MemberService
import com.ssak3.timeattack.notifications.domain.PushNotification
import com.ssak3.timeattack.task.service.TaskService
import com.ssak3.timeattack.task.service.events.DeleteTaskEvent
import com.ssak3.timeattack.task.service.events.ReminderSaveEvent
import com.ssak3.timeattack.task.service.events.ScheduledTaskSaveEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PushNotificationListener(
    private val pushNotificationService: PushNotificationService,
    private val memberService: MemberService,
    private val taskService: TaskService,
) : Logger {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun saveNotification(event: ScheduledTaskSaveEvent) {
        val member = memberService.getMemberById(event.memberId)
        val task = taskService.getTaskById(event.taskId)

        val pushNotification =
            PushNotification(
                member = member,
                task = task,
                scheduledAt = event.alarmTime,
                order = 0,
            )

        pushNotificationService.save(pushNotification)
    }

    @EventListener
    fun saveNotifications(event: ReminderSaveEvent) {
        val member = memberService.getMemberById(event.memberId)
        val task = taskService.getTaskById(event.taskId)

        val pushNotifications: List<PushNotification> =
            event.alarmTimes.map {
                PushNotification(
                    member = member,
                    task = task,
                    scheduledAt = it.alarmTime,
                    order = it.order,
                )
            }

        pushNotificationService.saveAll(pushNotifications)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun deleteNotifications(event: DeleteTaskEvent) {
//        logger.info("Delete notifications by task id: ${event.taskId}")
        TODO("알림 삭제 이벤트를 받아 db에서 비활성화 처리")
    }
}
