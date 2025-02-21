package com.ssak3.timeattack.member.auth.oidc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.ssak3.timeattack.global.exception.ApplicationException
import com.ssak3.timeattack.global.exception.ApplicationExceptionType.OIDC_PUBLIC_KEY_NOT_FOUND
import java.io.Serializable

/**
 * 카카오 인증 서버가 ID 토큰 서명 시 사용한 공개키 목록
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class OIDCPublicKeyList(
    @JsonProperty("keys")
    val keys: List<OIDCPublicKey>,
) : Serializable {
    /**
     * 주어진 kid(Key ID)와 alg(알고리즘)에 해당하는 OIDC 공개키를 찾아 반환
     */
    fun getMatchedKey(
        kid: String,
        alg: String,
    ): OIDCPublicKey =
        keys.find { it.kid == kid && it.alg == alg }
            ?: throw ApplicationException(OIDC_PUBLIC_KEY_NOT_FOUND)
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class OIDCPublicKey(
    @JsonProperty("kid")
    val kid: String,
    @JsonProperty("kty")
    val kty: String,
    @JsonProperty("alg")
    val alg: String,
    @JsonProperty("use")
    val use: String,
    @JsonProperty("n")
    val n: String,
    @JsonProperty("e")
    val e: String,
) : Serializable
