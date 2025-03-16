package com.ssak3.timeattack.member.auth.client

import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Component
@FeignClient(
    name = "kakaoApiClient",
    url = "https://kapi.kakao.com",
)
interface KakaoFeignClient {
    /**
     * 카카오 인증 서버에 연결된 계정과의 연결 해제
     */
    @PostMapping("/v1/user/unlink", consumes = ["application/x-www-form-urlencoded"])
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    fun unlink(
        @RequestHeader("Authorization") authorization: String,
        @RequestParam target_id_type: String = "user_id",
        @RequestParam target_id: String,
    ): KakaoUnlinkResponse
}
