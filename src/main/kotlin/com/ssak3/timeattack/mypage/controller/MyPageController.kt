package com.ssak3.timeattack.mypage.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.member.domain.Member
import com.ssak3.timeattack.mypage.controller.dto.MyPageResponse
import com.ssak3.timeattack.mypage.service.MyPageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "MyPage APIs")
@RestController
@RequestMapping("/v1/mypage")
class MyPageController(
    private val myPageService: MyPageService,
) {
    @Operation(summary = "마이페이지 조회", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @GetMapping
    fun myPage(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<MyPageResponse> {
        val myPageResponse = myPageService.myPage(member)

        return ResponseEntity.ok(myPageResponse)
    }
}
