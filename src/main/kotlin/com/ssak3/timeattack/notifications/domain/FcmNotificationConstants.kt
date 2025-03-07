package com.ssak3.timeattack.notifications.domain

import kotlin.random.Random

object FcmNotificationConstants {
    fun getMessage(order: Int): String {
        val messages = messageTemplate[order] ?: throw IllegalStateException("message not exist for this number")
        val index = Random.nextInt(messages.size)
        return messages[index]
    }

    fun getRoute(order: Int): String {
        return if (order == 0) {
            "/action/push"
        } else {
            "/action/push?left=${REMINDER_LIMIT - order}"
        }
    }

    private val messageTemplate =
        mapOf(
            0 to
                listOf(
                    """
                    작업 시간이 다 되었어요!
                    작은 행동부터 시작해볼까요?
                    """.trimIndent(),
                ),
            1 to
                listOf(
                    """
                    이제 두 번의 기회만 남았어요!
                    미루기 전에 얼른 시작해볼까요?
                    """.trimIndent(),
                ),
            2 to
                listOf(
                    """
                    한번만 더 알림오고 끝이에요!
                    작업을 미루기 전에 얼른 시작해보세요!
                    """.trimIndent(),
                ),
            3 to
                listOf(
                    """
                    이게 마지막 기회에요!
                    더 미루면 알림도 포기할거에요. 당장 시작하세요!
                    """.trimIndent(),
                ),
        )

    private const val REMINDER_LIMIT = 3
}
