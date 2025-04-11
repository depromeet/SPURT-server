package com.ssak3.timeattack.member.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.common.utils.checkNotNull
import com.ssak3.timeattack.member.controller.dto.MemberInfoResponse
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.notifications.service.FcmDeviceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/members")
class MemberController(
    private val fcmDeviceService: FcmDeviceService,
) {
    @Operation(summary = "현재 유저 정보 조회", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<MemberInfoResponse> {
        checkNotNull(member.id, "memberId")

        // FCM 디바이스 정보 조회
        val devices = fcmDeviceService.getDevicesByMember(member.id)
        return ResponseEntity.ok(
            MemberInfoResponse(
                memberId = member.id,
                nickname = member.nickname,
                email = member.email,
                profileImageUrl = member.profileImageUrl,
                hasFcmToken = devices.isNotEmpty(),
            ),
        )
    }
}
