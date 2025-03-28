package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.member.service.MemberService
import com.ssak3.timeattack.notifications.domain.PushNotification
import com.ssak3.timeattack.task.service.TaskService
import com.ssak3.timeattack.task.service.events.DeleteTaskNotificationEvent
import com.ssak3.timeattack.task.service.events.ReminderSaveEvent
import com.ssak3.timeattack.task.service.events.TriggerActionNotificationSaveEvent
import com.ssak3.timeattack.task.service.events.TriggerActionNotificationUpdateEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PushNotificationListener(
    private val pushNotificationService: PushNotificationService,
    private val memberService: MemberService,
    private val taskService: TaskService,
) : Logger {
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun saveNotification(event: TriggerActionNotificationSaveEvent) {
        logger.info("TriggerActionAlarmSaveEvent: $event")
        val member = memberService.getMemberById(event.memberId)
        val task = taskService.getTaskById(event.taskId)

        val pushNotification =
            PushNotification(
                member = member,
                task = task,
                scheduledAt = event.alarmTime.withSecond(0),
                order = 0,
            )

        pushNotificationService.save(pushNotification)
    }

    @EventListener
    fun saveNotifications(event: ReminderSaveEvent) {
        logger.info("ReminderSaveEvent: $event")
        val member = memberService.getMemberById(event.memberId)
        val task = taskService.getTaskById(event.taskId)

        val pushNotifications: List<PushNotification> =
            event.alarmTimes.map {
                PushNotification(
                    member = member,
                    task = task,
                    scheduledAt = it.alarmTime.withSecond(0),
                    order = it.order,
                )
            }

        pushNotificationService.saveAll(pushNotifications)
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun deleteNotifications(event: DeleteTaskNotificationEvent) {
        logger.info("DeleteTaskAlarmEvent: $event")
        val task = taskService.getTaskById(event.taskId)
        pushNotificationService.delete(task)
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun updateNotification(event: TriggerActionNotificationUpdateEvent) {
        val member = memberService.getMemberById(event.memberId)
        val task = taskService.getTaskById(event.taskId)

        // 기존 알림 제거
        pushNotificationService.delete(task)

        val pushNotification =
            PushNotification(
                member = member,
                task = task,
                scheduledAt = event.alarmTime.withSecond(0),
                order = 0,
            )
        pushNotificationService.save(pushNotification)
    }
}
