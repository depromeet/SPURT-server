package com.ssak3.timeattack.member.controller.dto

data class MemberInfoResponse(
    val memberId: Long,
    val nickname: String,
    val email: String,
    val profileImageUrl: String?,
    val hasFcmToken: Boolean,
)
