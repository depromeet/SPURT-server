package com.ssak3.timeattack.common.security

import com.ssak3.timeattack.member.domain.Member
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.time.Instant
import java.util.Date
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider(
    val jwtProperties: JwtProperties,
) {

    // accessToken, refreshToken 생성
    fun generateTokens(member: Member): JwtTokenDto {
        val now = Date.from(Instant.now())
        val accessToken = generateToken(member, now, jwtProperties.accessTokenValidityInSeconds)
        val refreshToken = generateToken(member, now, jwtProperties.refreshTokenValidityInSeconds)

        return JwtTokenDto(accessToken, refreshToken)
    }

    private fun generateToken(member: Member, now: Date, validity: Long): String =
        Jwts.builder()
            .setSubject(member.id.toString())
            .claim("nickname", member.nickname)
            .claim("email", member.email)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + validity))
            .signWith(jwtProperties.key)
            .compact()

}

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val header: String,
    val secret: String,
    val accessTokenValidityInSeconds: Long,
    val refreshTokenValidityInSeconds: Long,
) {
    val key: Key = Keys.hmacShaKeyFor(secret.toByteArray())
}
