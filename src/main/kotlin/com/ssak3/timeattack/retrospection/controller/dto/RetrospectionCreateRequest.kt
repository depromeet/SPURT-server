package com.ssak3.timeattack.retrospection.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.Size

@Schema(description = "회고 생성 요청")
data class RetrospectionCreateRequest(
    @Schema(
        title = "만족도",
        description = "순서대로 20, 40, 60, 80, 100 중 하나",
        example = "80",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:Size(min = 0, max = 100)
    val satisfaction: Int,
    @Schema(
        title = "집중도",
        description = "순서대로 0, 20, 40, 60, 80, 100 중 하나",
        example = "40",
        requiredMode = RequiredMode.REQUIRED,
    )
    @field:Size(min = 0, max = 100)
    val concentration: Int,
    @Schema(
        title = "회고 코멘트",
        example = "더 일찍 시작할껄..껄무새 등장!",
        requiredMode = RequiredMode.NOT_REQUIRED,
    )
    val comment: String?=null,
)
