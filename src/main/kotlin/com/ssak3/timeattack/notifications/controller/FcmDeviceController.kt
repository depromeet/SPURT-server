package com.ssak3.timeattack.notifications.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.common.dto.MessageResponse
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.notifications.controller.dto.FcmDeviceCreateRequest
import com.ssak3.timeattack.notifications.service.FcmDeviceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/fcm-devices")
class FcmDeviceController(
    private val fcmDeviceService: FcmDeviceService,
) {
    @Operation(summary = "사용자 fcm 디바이스 정보 저장", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PostMapping
    fun create(
        @AuthenticationPrincipal member: Member,
        @RequestBody @Valid request: FcmDeviceCreateRequest,
    ): ResponseEntity<MessageResponse> {
        fcmDeviceService.save(member, request)
        return ResponseEntity.ok(MessageResponse("Fcm device created successfully"))
    }
}
