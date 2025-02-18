package com.ssak3.timeattack.member.controller

import com.ssak3.timeattack.common.security.JwtTokenDto
import com.ssak3.timeattack.member.service.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun socialLogin(
        @RequestBody loginRequest: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        // 소셜 로그인 후, JWT 토큰 반환
        val tokens = authService.authenticateAndRegister(loginRequest)

        // access, refresh 토큰을 cookie에 저장
        setCookies(tokens, response)

        return ResponseEntity.ok("success")
    }

    private fun setCookies(tokens: JwtTokenDto, response: HttpServletResponse) {
        val accessTokenCookie = Cookie("accessToken", tokens.accessToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 24 * 60 * 60
        }
        val refreshTokenCookie = Cookie("refreshToken", tokens.refreshToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 7 * 24 * 60 * 60
        }

        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)
    }
}
