package com.ssak3.timeattack.member.auth

import com.ssak3.timeattack.member.oidc.OIDCPublicKeyList
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "kakaoAuthClient",
    url = "https://kauth.kakao.com",
)
interface KakaoFeignClient {

    /**
     * 인가 코드로 카카오 인증 서버에 ID token 요청하기
     */
    @PostMapping("/oauth/token")
    fun getToken(
        @RequestParam("grant_type") grantType: String = "authorization_code",
        @RequestParam("client_id") clientId: String,
        @RequestParam("redirect_uri") redirectUri: String,
        @RequestParam("code") code: String,
        @RequestParam("client_secret") clientSecret: String,
    ): OAuthTokenResponse


    /**
     * 카카오 인증 서버가 ID 토큰 서명 시 사용한 공개키 목록을 조회
     * TODO : 응답 값 Redis에 캐싱
     */
    @GetMapping("/.well-known/jwks.json")
    fun getPublicKeys(): OIDCPublicKeyList
}
