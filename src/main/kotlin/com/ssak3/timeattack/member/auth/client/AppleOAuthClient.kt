package com.ssak3.timeattack.member.auth.client

import com.ssak3.timeattack.common.utils.Logger
import com.ssak3.timeattack.member.auth.oidc.OIDCPublicKeyList
import com.ssak3.timeattack.member.auth.properties.AppleProperties
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
    @Autowired
    val appleFeignClient: AppleFeignClient,
    @Autowired
    val appleProperties: AppleProperties,
) : OAuthClient, Logger {
    fun getAuthCode() {
        logger.info(
            "애플 인증 URL 생성: clientId={}, redirectUri={}",
            appleProperties.clientId,
            appleProperties.redirectUri,
        )
        appleFeignClient.getAuthCode(
            clientId = appleProperties.clientId,
            redirectUri = appleProperties.redirectUri,
        )
    }

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

    // client-secret 생성
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
     * YAML 설정에서 가져온 private key 문자열을 PrivateKey 객체로 변환합니다.
     *
     * @return 변환된 PrivateKey 객체
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
}
