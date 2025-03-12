package com.ssak3.timeattack.common.config

import com.ssak3.timeattack.common.security.JwtAuthenticationFilter
import com.ssak3.timeattack.common.security.JwtTokenProvider
import com.ssak3.timeattack.member.service.MemberService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val corsProperties: CorsProperties,
    private val securityProperties: SecurityProperties,
    private val memberService: MemberService,
) {
    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(jwtTokenProvider, memberService, securityProperties)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration =
            CorsConfiguration().apply {
                corsProperties.allowedOrigins.forEach { addAllowedOrigin(it) }
                corsProperties.allowedHeaders.forEach { addAllowedHeader(it) }
                corsProperties.allowedMethods.forEach { addAllowedMethod(it) }
                allowCredentials = corsProperties.allowCredentials
            }

        // 모든 URL 패턴(**)에 위 설정 적용
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(*securityProperties.permitUrls.toTypedArray()).permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { it.disable() }
            .addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java,
            )

        return http.build()
    }
}

@ConfigurationProperties(prefix = "cors")
data class CorsProperties(
    val allowedOrigins: List<String>,
    val allowedHeaders: List<String>,
    val allowedMethods: List<String>,
    val allowCredentials: Boolean,
)

@ConfigurationProperties(prefix = "security")
data class SecurityProperties(
    val permitUrls: List<String>,
)
