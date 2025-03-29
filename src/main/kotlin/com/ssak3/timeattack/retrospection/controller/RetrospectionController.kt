package com.ssak3.timeattack.retrospection.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.retrospection.controller.dto.RetrospectionCreateRequest
import com.ssak3.timeattack.retrospection.service.RetrospectionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Retrospection APIs")
@RestController
@RequestMapping("/v1/tasks")
class RetrospectionController(
    private val retrospectionService: RetrospectionService,
) {
    @Operation(summary = "회고 생성", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @PostMapping("/{taskId}/retrospectives")
    fun createRetrospection(
        @AuthenticationPrincipal member: Member,
        @PathVariable taskId: Long,
        @RequestBody retrospectionRequest: RetrospectionCreateRequest,
    ): ResponseEntity<String> {
        retrospectionService.createRetrospection(retrospectionRequest, member, taskId)
        return ResponseEntity.ok("Retrospection created successfully")
    }
}
