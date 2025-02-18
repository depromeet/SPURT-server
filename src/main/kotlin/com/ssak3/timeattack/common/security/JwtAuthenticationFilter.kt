package com.ssak3.timeattack.common.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1. 쿠키에서 accessToken 가져오기
        val accessToken = resolveToken(request)

        // 2. accessToken 유효성 검증
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            val memberId = jwtTokenProvider.getMemberIdFromToken(accessToken)

        }
            // 2.1 유효 기간 지난 경우 -> 에러 던지기 -> 프론트에서는 refresh 토큰으로 accessToken 받는 api 호출
        // 3. memberId 추출
        // 4. SecurityContext에 저장
    }

    private fun resolveToken(request: HttpServletRequest) : String? {
        request.cookies?.forEach { cookie ->
            if (cookie.name == "accessToken") return cookie.value
        }
        return null
    }

    private fun setAuthenticationInSecurityContext(memberId: Long) {

    }

}