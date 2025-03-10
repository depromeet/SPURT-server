package com.ssak3.timeattack.member.auth.client

import com.fasterxml.jackson.annotation.JsonProperty

data class AppleAuthCodeResponse(
    // 인증 코드
    @JsonProperty("code")
    val code: String,
    // ID 토큰 (JWT)
    @JsonProperty("id_token")
    val idToken: String?,
    // 상태 값 (CSRF 방지용)
    @JsonProperty("state")
    val state: String?,
    // 사용자 정보 (선택적)
    @JsonProperty("user")
    val user: UserInfo?,
) {
    data class UserInfo(
        // 사용자 이메일
        @JsonProperty("email")
        val email: String?,
        // 사용자 이름 정보
        @JsonProperty("name")
        val name: NameInfo?,
    ) {
        data class NameInfo(
            @JsonProperty("firstName")
            val firstName: String?,
            @JsonProperty("lastName")
            val lastName: String?,
        )
    }
}
