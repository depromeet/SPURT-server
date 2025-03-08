package com.ssak3.timeattack.member.auth.oidc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_EXPIRED
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_GENERAL_ERR
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_INVALID_SIGNATURE
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_MALFORMED
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.JWT_UNSUPPORTED
import com.ssak3.timeattack.common.exception.ApplicationExceptionType.UNDEFINED_EXCEPTION
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64

@Component
class OIDCTokenVerification(
    private val objectMapper: ObjectMapper = jacksonObjectMapper(),
) {
    fun verifyIdToken(
        idToken: String,
        oidcPublicKeys: OIDCPublicKeyList,
    ): OIDCPayload {
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
        val keySpec =
            RSAPublicKeySpec(
                BigInteger(1, Base64.getUrlDecoder().decode(publicKey.n)),
                BigInteger(1, Base64.getUrlDecoder().decode(publicKey.e)),
            )
        return keyFactory.generatePublic(keySpec)
    }

    // ID Token 검증 및 페이로드 추출
    private fun verifyAndExtractPayload(
        token: String,
        publicKey: PublicKey,
    ): OIDCPayload {
        return try {
            val claims =
                Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .body

            OIDCPayload(
                subject = claims.subject,
                email = claims["email"] as String,
                picture = claims["picture"] as String,
                name = claims["nickname"] as String,
            )
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
}
