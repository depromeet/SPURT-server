package com.ssak3.timeattack.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig(
    @Value("\${spring.data.redis.host}") private val host: String,
    @Value("\${spring.data.redis.port}") private val port: Int,
    @Value("\${spring.data.redis.password}") private val password: String,
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration().apply {
            hostName = host
            port = this@RedisConfig.port
            password = RedisPassword.of(this@RedisConfig.password)
        }

        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisCacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager {
        val configuration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofDays(7))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJackson2JsonRedisSerializer()
                )
            )

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(configuration)
            .build()
    }
}
