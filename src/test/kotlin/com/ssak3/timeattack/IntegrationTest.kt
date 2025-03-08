package com.ssak3.timeattack

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.event.RecordApplicationEvents
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RecordApplicationEvents
@ExtendWith(MockKExtension::class)
@ExtendWith(SpringExtension::class)
annotation class IntegrationTest
