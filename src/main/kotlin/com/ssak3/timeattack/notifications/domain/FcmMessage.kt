package com.ssak3.timeattack.notifications.domain

import com.ssak3.timeattack.external.firebase.domain.DevicePlatform

data class FcmMessage(
    val token: String,
    val platform: DevicePlatform,
    val title: String,
    val body: String,
    val route: String,
)
