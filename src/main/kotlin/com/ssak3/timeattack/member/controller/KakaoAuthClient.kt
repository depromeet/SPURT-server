package com.ssak3.timeattack.member.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.ssak3.timeattack.member.oidc.OIDCPublicKeyList
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "kakaoAuthClient",
    url = "https://kauth.kakao.com",
)
interface KakaoAuthClient {

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
    ): KakaoTokenResponse


    /**
     * 카카오 인증 서버가 ID 토큰 서명 시 사용한 공개키 목록을 조회
     * TODO : 응답 값 Redis에 캐싱
     */
    @GetMapping("/.well-known/jwks.json")
    fun getPublicKeys(): OIDCPublicKeyList
}

data class KakaoTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("id_token")
    val idToken: String,

    @JsonProperty("expires_in")
    val expiresIn: Int
)