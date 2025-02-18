package com.ssak3.timeattack

import com.ssak3.timeattack.common.config.RedisProperties
import com.ssak3.timeattack.common.security.JwtProperties
import com.ssak3.timeattack.member.auth.properties.KakaoProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableConfigurationProperties(KakaoProperties::class, RedisProperties::class, JwtProperties::class)
@EnableFeignClients
class TimeAttackApplication

fun main(args: Array<String>) {
    runApplication<TimeAttackApplication>(*args)
}
