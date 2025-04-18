package com.ssak3.timeattack.member.repository.entity

import com.ssak3.timeattack.member.domain.OAuthProvider
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

class OAuthProviderInfo(
    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", length = 20)
    val oauthProvider: OAuthProvider,
    @Column(name = "subject", length = 255)
    val subject: String,
) {
    override fun toString(): String {
        return oauthProvider.toString()
    }

    constructor() : this(OAuthProvider.KAKAO, "")
}
