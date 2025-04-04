package com.ssak3.timeattack.persona.controller

import com.ssak3.timeattack.common.config.SwaggerConfig.Companion.SECURITY_SCHEME_NAME
import com.ssak3.timeattack.persona.controller.dto.PersonaPlaylistResponse
import com.ssak3.timeattack.persona.service.PersonaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/persona")
class PersonaController(
    private val personaService: PersonaService,
) {
    @Operation(summary = "페르소나에 맞는 플레이리스트 조회", security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)])
    @GetMapping("/{id}/playlists")
    fun getPlaylists(
        @PathVariable(required = true) @Positive id: Long,
    ): ResponseEntity<PersonaPlaylistResponse> {
        val playlistIds = personaService.getPlayListIds(id)
        val response = PersonaPlaylistResponse(playlistIds)
        return ResponseEntity.ok(response)
    }
}
