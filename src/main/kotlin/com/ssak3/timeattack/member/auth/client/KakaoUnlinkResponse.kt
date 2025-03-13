package com.ssak3.timeattack.member.auth.client

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUnlinkResponse (
    @JsonProperty("id")
    val subject: Long,
)
