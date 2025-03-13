package com.ssak3.timeattack.member.auth.client

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.member.auth.oidc.OIDCPublicKeyList
import com.ssak3.timeattack.member.auth.properties.AppleProperties
import com.ssak3.timeattack.member.domain.AppleAuthToken
import com.ssak3.timeattack.member.repository.AppleAuthTokenRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.StringReader
import java.security.PrivateKey
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Component
class AppleOAuthClient(
    val appleFeignClient: AppleFeignClient,
    val appleProperties: AppleProperties,
    val appleAuthTokenRepository: AppleAuthTokenRepository,
) : OAuthClient, Logger {
    override fun getToken(authCode: String): OAuthTokenResponse {
        return appleFeignClient.getToken(
            code = authCode,
            clientId = appleProperties.clientId,
            clientSecret = createClientSecret(),
            redirectUri = appleProperties.redirectUri,
        )
    }

    override fun getPublicKeys(): OIDCPublicKeyList {
        return appleFeignClient.getPublicKeys()
    }

    // identifier =  memberId
    override fun unlink(identifier: String) {
        val memberId = identifier.toLong()
        // DB에서 apple refresh token 조회
        val appleRefreshToken = getAppleRefreshToken(memberId)

        // 애플 연결 끊기 요청
        appleFeignClient.unlink(
            clientId = appleProperties.clientId,
            clientSecret = createClientSecret(),
            token = appleRefreshToken,
        )

        // DB에서 apple refresh token 삭제
        appleAuthTokenRepository.deleteById(memberId)
    }

    // client-secret(JWT 형식) 생성
    private fun createClientSecret(): String {
        // JWT 만료 시간 설정 (1시간)
        val expirationDate =
            Date.from(
                LocalDateTime.now()
                    .plusHours(1)
                    .atZone(ZoneId.systemDefault())
                    .toInstant(),
            )

        // JWT 생성
        val clientSecret =
            Jwts.builder()
                .setHeaderParam("kid", appleProperties.keyId)
                .setHeaderParam("alg", "ES256")
                .setIssuer(appleProperties.teamId)
                .setIssuedAt(Date())
                .setExpiration(expirationDate)
                .setAudience(appleProperties.aud)
                .setSubject(appleProperties.clientId)
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact()

        logger.info("Client Secret: $clientSecret")
        return clientSecret
    }

    /**
     * decoded private key를 PrivateKey 객체로 변환
     */
    private fun getPrivateKey(): PrivateKey {
        StringReader(appleProperties.getDecodePrivateKey()).use { pemReader ->
            PEMParser(pemReader).use { pemParser ->
                val converter = JcaPEMKeyConverter()
                val privateKeyInfo = pemParser.readObject() as PrivateKeyInfo
                return converter.getPrivateKey(privateKeyInfo)
            }
        }
    }

    private fun getAppleRefreshToken(memberId: Long): String {
        val appleAuthToken =
            AppleAuthToken.fromEntity(
                appleAuthTokenRepository.findById(memberId).orElseThrow {
                    ApplicationException(ApplicationExceptionType.APPLE_REFRESH_TOKEN_NOT_FOUND)
                },
            )
        return appleAuthToken.refreshToken
    }
}
