package com.ssak3.timeattack.member.controller

import com.ssak3.timeattack.common.security.JwtTokenDto
import com.ssak3.timeattack.common.security.refreshtoken.RefreshTokenService
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val refreshTokenService: RefreshTokenService,
) {
    @Operation(summary = "소셜 로그인", description = "소셜 로그인 후 JWT access, refresh 토큰 반환")
    @PostMapping("/login")
    fun socialLogin(
        @RequestBody loginRequest: LoginRequest,
        response: HttpServletResponse,
    ): ResponseEntity<LoginResponse> {
        // 소셜 로그인 후, JWT 토큰 반환
        val (tokens, isNewUser) = authService.authenticateAndRegister(loginRequest)

        return ResponseEntity.ok(LoginResponse(tokens, isNewUser))
    }

    @Operation(summary = "인증 필터 테스트", security = [SecurityRequirement(name = "BearerAuth")])
    @GetMapping("/test")
    fun testFilter(
        @AuthenticationPrincipal member: Member,
    ): Member = member

    @Operation(summary = "JWT 토큰 재발급", description = "refresh 토큰을 이용하여 access, refresh 토큰 재발급")
    @PostMapping("/token/refresh")
    fun refreshAccessToken(
        @RequestBody refreshRequest: RefreshRequest,
    ): JwtTokenDto {
        return refreshTokenService.reissueTokens(refreshRequest.refreshToken)
    }
}
