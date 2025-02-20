package com.ssak3.timeattack

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients
class TimeAttackApplication

fun main(args: Array<String>) {
    runApplication<TimeAttackApplication>(*args)
}
