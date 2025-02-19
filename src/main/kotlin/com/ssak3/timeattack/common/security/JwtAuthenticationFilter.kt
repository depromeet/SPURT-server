package com.ssak3.timeattack.common.security

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType.JWT_NOT_FOUND
import com.ssak3.timeattack.member.infrastructure.MemberRepository
import com.ssak3.timeattack.member.infrastructure.findByIdOrThrow
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter


class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberRepository: MemberRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestUri = request.requestURI

        // 공개 엔드포인트인 경우 토큰 체크를 하지 않고 필터 체인 진행
        if (publicEndpoints.contains(requestUri)) {
            filterChain.doFilter(request, response)
            return
        }

        // 쿠키에서 accessToken 가져오기
        val accessToken = resolveToken(request) ?: throw ApplicationException(JWT_NOT_FOUND)

        // accessToken 유효성 검증
        if (jwtTokenProvider.validateToken(accessToken)) {
            val memberId = jwtTokenProvider.getMemberIdFromToken(accessToken)
            setAuthenticationInSecurityContext(memberId)
            log.info("SecurityContextHolder 설정 성공: ${SecurityContextHolder.getContext().authentication}")
        }

        // 유효기간 지난 경우, getClaims()에서 JWT_EXPIRED 예외 발생

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        request.cookies?.forEach { cookie ->
            if (cookie.name == "accessToken") return cookie.value
        }
        return null
    }

    // member 조회하고 securityContextHolder
    private fun setAuthenticationInSecurityContext(memberId: Long) {
        val member = memberRepository.findByIdOrThrow(memberId)
        val authenticationToken = UsernamePasswordAuthenticationToken(member, null, ArrayList())

        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    companion object {
        // 공개 엔드 포인트
        private val publicEndpoints = listOf(
            "/oauth/login",
            "/oauth/refresh",
            "/oauth/kakao",
            "/oauth/kakao/callback"
        )

        private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }
}
