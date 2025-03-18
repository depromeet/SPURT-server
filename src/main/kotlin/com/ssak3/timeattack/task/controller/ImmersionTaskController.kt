package com.ssak3.timeattack.task.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.task.controller.dto.ImmersionResponse
import com.ssak3.timeattack.task.service.ImmersionTaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/immersion-tasks")
class ImmersionTaskController(
    private val immersionTaskService: ImmersionTaskService,
) {
    @Operation(summary = "몰입 중인 작업 목록 호출", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @GetMapping("/all")
    fun getImmersionTasks(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<ImmersionResponse> {
        val immersionTasks = immersionTaskService.getImmersionTasks(member)
        val response = ImmersionResponse(immersionTasks = immersionTasks)
        return ResponseEntity.ok(response)
    }
}
