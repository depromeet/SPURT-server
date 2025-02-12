package com.ssak3.timeattack.member.auth.oidc

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

/**
 * id token을 검증하는 클래스
 */
@Component
class OIDCTokenVerification {

    /**
     * ID Token 검증 메서드
     */
    fun verifyIdToken(idToken: String, oidcPublicKeys: OIDCPublicKeyList): OIDCPayload {
        val (kid, alg) = extractFromHeader(idToken)
        val matchedKey = oidcPublicKeys.getMatchedKey(kid, alg)
        val publicKey = createPublicKey(matchedKey)
        return verifyAndExtractPayload(idToken, publicKey)
    }

    /**
     * 토큰 헤더에서 kid 및 alg 추출
     * kid : ID 토큰이 어떤 공개키로 서명되었는지 식별하는 키
     * alg : ID 토큰이 서명된 알고리즘 (RS256 등)
     * (kid, alg) 반환
     */
    private fun extractFromHeader(token: String): Pair<String, String> {
        val headerJson = String(Base64.getDecoder().decode(token.split(".")[0]))
        val header = ObjectMapper().readValue(headerJson, Map::class.java)
        return (header["kid"] as String) to (header["alg"] as String)
    }

    /**
     * JWT 서명을 검증할 RSA 공개키를 생성
     */
    private fun createPublicKey(publicKey: OIDCPublicKey): PublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = RSAPublicKeySpec(
            BigInteger(1, Base64.getUrlDecoder().decode(publicKey.n)),
            BigInteger(1, Base64.getUrlDecoder().decode(publicKey.e))
        )
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * 토큰 서명을 검증하고 페이로드를 추출
     * TODO: 공통 에러 응답으로 수정하기
     */
    private fun verifyAndExtractPayload(token: String, publicKey: PublicKey): OIDCPayload {
        return try {
            // id token을 공개키로 서명 검증으로 클레임 추출
            val claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .body

            // JWT 페이로드에서 사용자 정보를 추출하여 OIDCPayload로 변환
            OIDCPayload(
                sub = claims.subject,
                email = claims["email"] as? String,
                picture = claims["picture"] as? String,
                name = claims["name"] as? String
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("ID token validation failed: ${e.message}")
        }
    }

}