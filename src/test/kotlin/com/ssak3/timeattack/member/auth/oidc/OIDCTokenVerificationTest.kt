package com.ssak3.timeattack.member.auth.oidc

import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType.JWT_EXPIRED
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey
import java.util.Date
import java.util.Base64
import org.junit.jupiter.api.DisplayName

class OIDCTokenVerificationTest {

    private lateinit var oidcTokenVerification: OIDCTokenVerification
    private lateinit var keyPair: KeyPair
    private lateinit var validToken: String
    private lateinit var expiredToken: String
    private lateinit var oidcPublicKey: OIDCPublicKey

    @BeforeEach
    fun setup() {
        oidcTokenVerification = OIDCTokenVerification()
        keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256)

        // 유효한 토큰 생성
        validToken = Jwts.builder()
            .setSubject("1234567890")
            .setHeaderParam("kid", "test-kid")
            .setHeaderParam("alg", "RS256")
            .claim("email", "test@example.com")
            .claim("nickname", "John Doe")
            .claim("picture", "https://example.com/picture.jpg")
            .setExpiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(keyPair.private, SignatureAlgorithm.RS256)
            .compact()

        // 만료된 토큰 생성
        expiredToken = Jwts.builder()
            .setSubject("1234567891")
            .setHeaderParam("kid", "test-kid")
            .setHeaderParam("alg", "RS256")
            .setExpiration(Date(System.currentTimeMillis() - 1000)) // 이미 만료됨
            .signWith(keyPair.private, SignatureAlgorithm.RS256)
            .compact()

        // RSA 공개키에서 실제 모듈러스 추출 후 Base64Url 인코딩
        val rsaPublicKey = keyPair.public as RSAPublicKey
        var modulusBytes = rsaPublicKey.modulus.toByteArray()

        // BigInteger의 toByteArray()는 부호 비트를 포함할 수 있으므로, 앞에 불필요한 0 바이트가 있으면 제거
        if (modulusBytes[0] == 0.toByte()) {
            modulusBytes = modulusBytes.copyOfRange(1, modulusBytes.size)
        }
        val modulusBase64Url = Base64.getUrlEncoder().withoutPadding().encodeToString(modulusBytes)

        // 수정된 공개키 정보를 사용
        oidcPublicKey = OIDCPublicKey(
            kid = "test-kid",
            alg = "RS256",
            n = modulusBase64Url,
            e = "AQAB",
            kty = "RSA",
            use = "sig",
        )
    }

    @Test
    @DisplayName("유효한 ID 토큰 검증 테스트")
    fun verifyIdToken_WithValidToken_ShouldReturnExpectedPayload() {
        // given
        val oidcPublicKeyList = OIDCPublicKeyList(listOf(oidcPublicKey))


        // when
        val payload = oidcTokenVerification.verifyIdToken(validToken, oidcPublicKeyList)

        // then
        assertEquals("1234567890", payload.subject)
        assertEquals("https://example.com/picture.jpg", payload.picture)
    }

    @Test
    @DisplayName("만료된 ID 토큰 검증 테스트")
    fun verifyIdToken_WithExpiredToken_ShouldThrowException() {
        // given
        val oidcPublicKeyList = OIDCPublicKeyList(listOf(oidcPublicKey))

        // when & then
        assertThrows<ApplicationException> {
            oidcTokenVerification.verifyIdToken(expiredToken, oidcPublicKeyList)
        }.apply {
            assertThat(this.exceptionType).isEqualTo(JWT_EXPIRED)
        }
    }
}

