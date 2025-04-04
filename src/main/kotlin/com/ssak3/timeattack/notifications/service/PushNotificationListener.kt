package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.member.service.MemberService
import com.ssak3.timeattack.notifications.domain.FcmNotificationConstants.getMessage
import com.ssak3.timeattack.notifications.domain.FcmNotificationConstants.getRemindMessage
import com.ssak3.timeattack.notifications.domain.FcmNotificationConstants.getSupportMessage
import com.ssak3.timeattack.notifications.domain.PushNotification
import com.ssak3.timeattack.task.service.TaskService
import com.ssak3.timeattack.task.service.events.DeleteTaskNotificationEvent
import com.ssak3.timeattack.task.service.events.ReminderSaveEvent
import com.ssak3.timeattack.task.service.events.SupportNotificationSaveEvent
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

        val pushNotifications =
            event.alarmTimes.mapIndexed { i, alarmTime ->
                PushNotification(
                    member = member,
                    task = task,
                    scheduledAt = alarmTime,
                    order = 0,
                    message = getMessage(i),
                )
            }

        pushNotificationService.saveAll(pushNotifications)
    }

    @EventListener
    fun saveNotifications(event: ReminderSaveEvent) {
        logger.info("ReminderSaveEvent: $event")
        val member = memberService.getMemberById(event.memberId)
        val task = taskService.getTaskById(event.taskId)

        // 기존 알림 제거
        pushNotificationService.delete(task)

        // 리마인드 알림 등록
        val pushNotifications: List<PushNotification> =
            event.alarmTimes.mapIndexed { i, alarmTime ->
                PushNotification(
                    member = member,
                    task = task,
                    scheduledAt = alarmTime,
                    order = i + 1,
                    message = getRemindMessage(i),
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
                message = getMessage(0),
            )
        pushNotificationService.save(pushNotification)
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun saveSupportNotifications(event: SupportNotificationSaveEvent) {
        logger.info("SupportNotificationSaveEvent: $event")

        val member = memberService.getMemberById(event.memberId)
        val task = taskService.getTaskById(event.taskId)

        // 기존 알림 제거
        pushNotificationService.delete(task)

        val pushNotifications: List<PushNotification> =
            event.alarmTimes.map {
                PushNotification(
                    member = member,
                    task = task,
                    scheduledAt = it.alarmTime.withSecond(0),
                    order = -1,
                    message =
                        getSupportMessage(
                            personaId = task.persona.id.toInt(),
                            personaName = task.persona.name,
                            nickname = member.nickname,
                            index = it.index,
                        ),
                )
            }

        pushNotificationService.saveAll(pushNotifications)
    }
}
