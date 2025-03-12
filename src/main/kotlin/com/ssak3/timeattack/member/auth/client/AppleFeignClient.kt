package com.ssak3.timeattack.member.auth.client

import com.ssak3.timeattack.common.constant.CacheConst.OIDC_PUBLIC_KEYS
import com.ssak3.timeattack.member.auth.oidc.OIDCPublicKeyList
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Component
@FeignClient(
    name = "appleAuthClient",
    url = "https://appleid.apple.com",
)
interface AppleFeignClient {
    /**
     * 인가 코드로 애플 인증 서버에 ID token 요청하기
     */
    @PostMapping("/auth/token")
    fun getToken(
        @RequestParam("grant_type") grantType: String = "authorization_code",
        @RequestParam("client_id") clientId: String,
        @RequestParam("redirect_uri") redirectUri: String,
        @RequestParam("code") code: String,
        @RequestParam("client_secret") clientSecret: String,
    ): OAuthTokenResponse

    /**
     * 애플 인증 서버가 ID 토큰 서명 시 사용한 공개키 목록을 조회
     * 조회 결과 Redis에 캐시
     */
    @GetMapping("/auth/keys")
    @Cacheable(value = [OIDC_PUBLIC_KEYS], key = "'apple'")
    fun getPublicKeys(): OIDCPublicKeyList

    @PostMapping("/auth/revoke")
    fun unlink(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("token") token: String,
        @RequestParam("token_type_hint") tokenTypeHint: String = "refresh_token",
    )
}
