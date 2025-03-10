package com.ssak3.timeattack.member.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ssak3.timeattack.common.domain.DevicePlatform
import com.ssak3.timeattack.common.security.JwtTokenDto
import com.ssak3.timeattack.common.security.refreshtoken.RefreshTokenService
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.member.auth.client.AppleAuthCodeResponse.UserInfo
import com.ssak3.timeattack.member.auth.properties.AppleProperties
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.net.URLEncoder
import java.util.UUID

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val refreshTokenService: RefreshTokenService,
    private val appleProperties: AppleProperties,
    private val objectMapper: ObjectMapper,
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

    @Operation(summary = "애플 로그인", description = "애플 로그인 후 JWT access, refresh 토큰 반환")
    @PostMapping("/login/apple")
    fun appleLogin(
        @RequestBody appleLoginRequest: AppleLoginRequest,
    ): ResponseEntity<LoginResponse> {
        logger.info("Apple Login Request: $appleLoginRequest")
        val loginResult = authService.authenticateAndRegister(appleLoginRequest)

        return ResponseEntity.ok(LoginResponse(loginResult.jwtTokenDto, loginResult.isNewUser, loginResult.memberInfo))
    }

    @GetMapping("/test-login/apple")
    fun testAppleLogin(): RedirectView {
        logger.info("========== 애플 로그인 페이지로 리다이렉트")

        // 애플 로그인 URL 생성
        val authUrl =
            "https://appleid.apple.com/auth/authorize" +
                "?response_type=code" +
                "&client_id=${appleProperties.clientId}" +
                "&redirect_uri=${URLEncoder.encode(appleProperties.redirectUri, "UTF-8")}" +
                "&response_mode=form_post" +
                "&scope=name email" +
                "&state=${UUID.randomUUID()}"

        logger.info("애플 로그인 URL: $authUrl")

        // 생성된 URL로 리다이렉트
        return RedirectView(authUrl)
    }

    @PostMapping("/callback/apple")
    fun appleCallback(
        @RequestParam("code") code: String,
        @RequestParam("id_token", required = false) idToken: String?,
        @RequestParam("state", required = false) state: String?,
        @RequestParam("user", required = false) userJson: String?,
    ): ResponseEntity<LoginResponse> {
        logger.info(
            "===== Apple Callback Parameters: code={}, idToken={}, state={}, userJson={}",
            code.take(10) + "...",
            idToken?.take(10) + "...",
            state,
            userJson,
        )

        // user JSON 문자열을 파싱 (존재하는 경우)
        val user =
            userJson?.let {
                try {
                    objectMapper.readValue(it, UserInfo::class.java)
                } catch (e: Exception) {
                    logger.warn("========== Failed to parse user JSON: {}", e.message, e)
                    null
                }
            }
        logger.info("Apple Login Callback: code=$code, idToken=$idToken, state=$state, user=$user")
        val appleLoginRequest =
            AppleLoginRequest(
                code,
                user?.name?.lastName + user?.name?.firstName,
                user?.email,
                "test-device-id",
                DevicePlatform.IOS,
            )

        val loginResult = authService.authenticateAndRegister(appleLoginRequest)

        return ResponseEntity.ok(LoginResponse(loginResult.jwtTokenDto, loginResult.isNewUser, loginResult.memberInfo))
    }
}
