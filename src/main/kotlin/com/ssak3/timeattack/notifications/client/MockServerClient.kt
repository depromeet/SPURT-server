package com.ssak3.timeattack.notifications.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping

@Component
@FeignClient(
    name = "mockerServerClient",
    url = "https://b38da56d-1531-48f9-8ffa-e79114e64f31.mock.pstmn.io/fcm-message/send",
)
interface MockServerClient {
    /**
     * mock server 를 통한 fcm 메시지 발송
     */
    @PostMapping
    fun sendFcmMessageMock()
}
