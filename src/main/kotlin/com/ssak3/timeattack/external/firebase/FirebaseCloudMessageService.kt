package com.ssak3.timeattack.external.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.notifications.domain.FcmMessage
import org.springframework.stereotype.Service

@Service
class FirebaseCloudMessageService : Logger {
    fun send(message: FcmMessage): String {
        logger.info("[Firebase Messaging] start to send: $message")
        val firebaseMessaging = FirebaseMessaging.getInstance()

        val fcmMessage =
            Message.builder()
                .setToken(message.token)
                .setNotification(
                    Notification.builder().setTitle(TITLE).setBody(message.body).build(),
                )
                .putAllData(
                    mapOf(
                        "route" to message.route,
                        "taskId" to message.taskId.toString(),
                    ),
                )
                .build()

        var response = ""
        try {
            response = firebaseMessaging.send(fcmMessage)
        } catch (exception: FirebaseMessagingException) {
            logger.error("[${exception.errorCode}] Error sending message: ${exception.message}", exception)
            // TODO: 잘못된 토큰 에러 발생 시 토큰 삭제 로직 추가
            // TODO: 토큰 이외에 실패시 메시지 큐등을 활용해 재시도 로직 추가
        }

        return response
    }

    companion object {
        const val TITLE = "SPURT"
    }
}
