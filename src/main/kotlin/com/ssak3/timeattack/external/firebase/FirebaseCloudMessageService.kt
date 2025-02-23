package com.ssak3.timeattack.external.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.notifications.domain.FcmMessage
import org.springframework.stereotype.Service

@Service
class FirebaseCloudMessageService : Logger {
    fun send(message: FcmMessage): String {
        val firebaseMessaging = FirebaseMessaging.getInstance()

        val fcmMessage =
            Message.builder()
                .setToken(message.token)
                .setNotification(
                    Notification.builder().setTitle(message.title).setBody(message.body).build(),
                )
                .putData("route", message.route)
                .build()

        val response = firebaseMessaging.send(fcmMessage)

        logger.info("[Firebase Messaging] send successfully: $response")

        return response
    }
}
