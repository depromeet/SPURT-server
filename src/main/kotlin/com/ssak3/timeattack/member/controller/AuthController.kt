package com.ssak3.timeattack.member.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.common.security.JwtTokenDto
import com.ssak3.timeattack.common.security.refreshtoken.RefreshTokenService
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.controller.dto.AppleLoginRequest
import com.ssak3.timeattack.member.controller.dto.LoginRequest
import com.ssak3.timeattack.member.controller.dto.LoginResponse
import com.ssak3.timeattack.member.controller.dto.RefreshRequest
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.member.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val refreshTokenService: RefreshTokenService,
) : Logger {
    @Operation(summary = "소셜 로그인", description = "소셜 로그인 후 JWT access, refresh 토큰 반환")
    @PostMapping("/login")
    fun socialLogin(
        @RequestBody loginRequest: LoginRequest,
        response: HttpServletResponse,
    ): ResponseEntity<LoginResponse> {
        // 소셜 로그인 후, JWT 토큰 반환
        val loginResult = authService.authenticateAndRegister(loginRequest)

        return ResponseEntity.ok(LoginResponse(loginResult.jwtTokenDto, loginResult.isNewUser, loginResult.memberInfo))
    }

    @Operation(summary = "JWT 토큰 재발급", description = "refresh 토큰을 이용하여 access, refresh 토큰 재발급")
    @PostMapping("/token/refresh")
    fun refreshAccessToken(
        @RequestBody refreshRequest: RefreshRequest,
    ): JwtTokenDto {
        return refreshTokenService.reissueTokens(refreshRequest.refreshToken)
    }

    @Operation(summary = "애플 로그인", description = "애플 로그인 후 JWT access, refresh 토큰 반환")
    @PostMapping("/login/apple")
    fun appleLogin(
        @RequestBody appleLoginRequest: AppleLoginRequest,
    ): ResponseEntity<LoginResponse> {
        logger.info("Apple Login Request: $appleLoginRequest")
        val loginResult = authService.authenticateAndRegister(appleLoginRequest)

        return ResponseEntity.ok(LoginResponse(loginResult.jwtTokenDto, loginResult.isNewUser, loginResult.memberInfo))
    }

    @Operation(summary = "로그아웃", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Void> {
        checkNotNull(member.id, "memberId")
        refreshTokenService.deleteRefreshToken(member.id)

        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "회원 탈퇴", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PostMapping("/withdraw")
    fun withdraw(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Void> {
        authService.withdraw(member)

        return ResponseEntity.noContent().build()
    }
}
