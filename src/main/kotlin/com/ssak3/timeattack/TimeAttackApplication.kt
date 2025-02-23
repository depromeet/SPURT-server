package com.ssak3.timeattack

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients
@EnableJpaAuditing
class TimeAttackApplication

fun main(args: Array<String>) {
    runApplication<TimeAttackApplication>(*args)
}
