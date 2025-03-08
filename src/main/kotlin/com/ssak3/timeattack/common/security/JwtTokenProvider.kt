package com.ssak3.timeattack.common.security

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_EXPIRED
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_GENERAL_ERR
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_INVALID_SIGNATURE
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_MALFORMED
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_UNSUPPORTED
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.UNDEFINED_EXCEPTION
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.security.Key
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Component
class JwtTokenProvider(
    val jwtProperties: JwtProperties,
) {
    // accessToken, refreshToken 생성
    fun generateTokens(memberId: Long): JwtTokenDto {
        val now = LocalDateTime.now()
        val accessToken = generateToken(memberId, now, jwtProperties.accessTokenValidityInSeconds)
        val refreshToken = generateToken(memberId, now, jwtProperties.refreshTokenValidityInSeconds)

        return JwtTokenDto(accessToken, refreshToken)
    }

    // 토큰 검증 (유효 기간 안지난 토큰인지)
    fun validateToken(token: String): Boolean {
        val claims = getClaims(token)
        return !claims.body.expiration.before(Date())
    }

    // 토큰에서 memberId 가져오기
    fun getMemberIdFromToken(token: String): Long {
        val claims = getClaims(token)
        return claims.body.subject.toLong()
    }

    // 토큰 생성
    private fun generateToken(
        memberId: Long,
        now: LocalDateTime,
        validityInSeconds: Long,
    ): String {
        val expiration = now.plusSeconds(validityInSeconds)
        return Jwts.builder()
            .setSubject(memberId.toString())
            .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
            .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(jwtProperties.key)
            .compact()
    }

    // token에서 claim 정보 추출하기
    private fun getClaims(token: String): Jws<Claims> =
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtProperties.key)
                .build()
                .parseClaimsJws(token)
        } catch (e: SignatureException) {
            throw ApplicationException(JWT_INVALID_SIGNATURE, e)
        } catch (e: ExpiredJwtException) {
            throw ApplicationException(JWT_EXPIRED, e)
        } catch (e: MalformedJwtException) {
            throw ApplicationException(JWT_MALFORMED, e)
        } catch (e: UnsupportedJwtException) {
            throw ApplicationException(JWT_UNSUPPORTED, e)
        } catch (e: JwtException) {
            throw ApplicationException(JWT_GENERAL_ERR, e)
        } catch (e: Exception) {
            throw ApplicationException(UNDEFINED_EXCEPTION, e)
        }
}

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenValidityInSeconds: Long,
    val refreshTokenValidityInSeconds: Long,
) {
    val key: Key = Keys.hmacShaKeyFor(secret.toByteArray())
}
