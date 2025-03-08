package com.ssak3.timeattack

import com.ssak3.timeattack.common.config.QueryDslConfig
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DataJpaTest
@Import(QueryDslConfig::class)
@ActiveProfiles("test")
annotation class RepositoryTest()
