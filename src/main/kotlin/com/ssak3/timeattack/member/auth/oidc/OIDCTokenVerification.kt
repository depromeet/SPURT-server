package com.ssak3.timeattack.member.auth.oidc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType.*
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SignatureException
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class OIDCTokenVerification(
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {

    fun verifyIdToken(idToken: String, oidcPublicKeys: OIDCPublicKeyList): OIDCPayload {
        val (kid, alg) = extractFromHeader(idToken)
        val matchedKey = oidcPublicKeys.getMatchedKey(kid, alg)
        val publicKey = createPublicKey(matchedKey)
        return verifyAndExtractPayload(idToken, publicKey)
    }

    // 토큰 헤더에서 kid 및 alg 추출
    private fun extractFromHeader(token: String): Pair<String, String> {
        val headerJson = String(Base64.getDecoder().decode(token.split(".")[0]))
        val header = objectMapper.readValue(headerJson, Map::class.java)
        return (header["kid"] as String) to (header["alg"] as String)
    }

    // RSA 공개키 생성
    private fun createPublicKey(publicKey: OIDCPublicKey): PublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = RSAPublicKeySpec(
            BigInteger(1, Base64.getUrlDecoder().decode(publicKey.module)),
            BigInteger(1, Base64.getUrlDecoder().decode(publicKey.exponent))
        )
        return keyFactory.generatePublic(keySpec)
    }

    // ID Token 검증 및 페이로드 추출
    private fun verifyAndExtractPayload(token: String, publicKey: PublicKey): OIDCPayload {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .body

            OIDCPayload(
                subject = claims.subject,
                email = claims["email"] as String,
                picture = claims["picture"] as String,
                name = claims["name"] as String
            )
        } catch (e: SignatureException) {
            throw ApplicationException(JWT_INVALID_SIGNATURE, e)
        } catch (e: ExpiredJwtException) {
            throw ApplicationException(JWT_EXPIRED, e)
        } catch (e: Exception) {
            throw ApplicationException(UNDEFINED_EXCEPTION, e)
        }
    }
}
