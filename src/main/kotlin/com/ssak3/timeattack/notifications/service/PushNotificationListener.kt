package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.member.service.MemberService
import com.ssak3.timeattack.notifications.domain.PushNotification
import com.ssak3.timeattack.task.service.TaskService
import com.ssak3.timeattack.task.service.events.DeleteTaskEvent
import com.ssak3.timeattack.task.service.events.ReminderSaveEvent
import com.ssak3.timeattack.task.service.events.ScheduledTaskSaveEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class PushNotificationListener(
    private val pushNotificationService: PushNotificationService,
    private val memberService: MemberService,
    private val taskService: TaskService,
) {
    @EventListener
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

    @EventListener
    fun deleteNotifications(event: DeleteTaskEvent) {
        TODO("알림 삭제 이벤트를 받아 db에서 비활성화 처리")
    }
}
