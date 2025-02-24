package com.ssak3.timeattack.notifications.controller.dto

data class PushNotificationSendRequest(
    val token: String,
    val platform: String,
    val title: String,
    val body: String,
    val route: String,
)
