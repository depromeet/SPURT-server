package com.ssak3.timeattack.member.auth.oidc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 카카오 인증 서버가 ID 토큰 서명 시 사용한 공개키 목록
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class OIDCPublicKeyList(
    @JsonProperty("keys")
    val keys: List<OIDCPublicKey>
) : Serializable {

    /**
     * id token의 kid, alg와 맞는 공개키 찾기
     * TODO : 공통 에러 response로 수정하기
     */
    fun getMatchedKey(kid: String, alg: String): OIDCPublicKey =
        keys.find { it.kid == kid && it.alg == alg }
            ?: throw IllegalArgumentException("일치하는 공개키를 찾을 수 없습니다.")
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
    val e: String
) : Serializable