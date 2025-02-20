package com.ssak3.timeattack.member.controller

import com.ssak3.timeattack.common.utils.JwtCookieUtil.Companion.setJwtTokenCookies
import com.ssak3.timeattack.member.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/oauth")
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
        setJwtTokenCookies(tokens, response)

        return ResponseEntity.ok("success")
    }

}
