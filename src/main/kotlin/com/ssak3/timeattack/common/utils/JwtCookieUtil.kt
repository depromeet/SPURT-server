package com.ssak3.timeattack.common.utils

import com.ssak3.timeattack.common.security.JwtTokenDto
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

class JwtCookieUtil {
    companion object {
        fun setJwtTokenCookies(tokens: JwtTokenDto, response: HttpServletResponse) {
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
}