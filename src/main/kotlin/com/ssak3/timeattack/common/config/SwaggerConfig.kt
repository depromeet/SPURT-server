package com.ssak3.timeattack.common.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(info = Info(title = "SPURT API", version = "1.0"))
@SecurityScheme(
    name = "CookieAuth",
    type = SecuritySchemeType.APIKEY,
    `in` = SecuritySchemeIn.COOKIE,
    paramName = "accessToken",
    description = "JWT Token stored in cookie"
)
class SwaggerConfig {

    @Value("\${server.url}")
    private val serverUrl: String? = null


    @Bean
    fun openAPI(): OpenAPI {
        val server = Server()
        server.url = serverUrl

        return OpenAPI()
            .addServersItem(server)
    }
}