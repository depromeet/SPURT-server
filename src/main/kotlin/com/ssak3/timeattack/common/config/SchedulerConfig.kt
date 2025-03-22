package com.ssak3.timeattack.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class SchedulerConfig {
    @Bean
    fun taskScheduler(): TaskScheduler {
        val taskScheduler = ThreadPoolTaskScheduler()
        taskScheduler.poolSize = 3
        taskScheduler.setThreadNamePrefix("task-scheduler-")
        taskScheduler.initialize()
        return taskScheduler
    }
}
