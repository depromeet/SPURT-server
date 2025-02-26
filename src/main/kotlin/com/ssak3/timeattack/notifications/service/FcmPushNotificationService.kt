package com.ssak3.timeattack.notifications.service

import com.ssak3.timeattack.external.firebase.FirebaseCloudMessageService
import com.ssak3.timeattack.notifications.domain.FcmMessage
import org.springframework.stereotype.Service

@Service
class FcmPushNotificationService(
    private val firebaseCloudMessageService: FirebaseCloudMessageService,
) {
    fun sendNotification(message: FcmMessage) = firebaseCloudMessageService.send(message)
}
