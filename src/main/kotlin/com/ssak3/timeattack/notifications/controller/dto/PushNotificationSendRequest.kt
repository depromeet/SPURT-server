package com.ssak3.timeattack.notifications.controller.dto

data class PushNotificationSendRequest(
    val token: String,
    val platform: String,
    val taskId: String,
    val body: String,
    val route: String,
    val title: String = "SPURT",
)
