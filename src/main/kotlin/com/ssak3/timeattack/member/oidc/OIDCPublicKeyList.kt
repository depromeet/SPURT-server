package com.ssak3.timeattack.member.oidc

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
) : Serializable

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