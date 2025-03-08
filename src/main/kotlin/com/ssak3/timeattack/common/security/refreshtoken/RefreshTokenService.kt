package com.ssak3.timeattack.common.security.refreshtoken

import com.ssak3.timeattack.common.exception.ApplicationException
import com.ssak3.timeattack.common.exception.ApplicationExceptionType
import com.ssak3.timeattack.common.security.JwtTokenDto
import com.ssak3.timeattack.common.security.JwtTokenProvider
import org.springframework.stereotype.Service

@Service
class RefreshTokenService(
    private val refreshTokenRedisRepository: RefreshTokenRedisRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    /**
     * refresh token을 이용하여 access token과 refresh token을 재발급한다.
     * 재발급한 refresh token은 Redis에 저장한다.
     */
    fun reissueTokens(refreshToken: String): JwtTokenDto {
        // refresh token 검증 & memberId 추출
        val memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken)

        // Redis에서 refresh token 조회
        val refreshTokenEntity =
            refreshTokenRedisRepository.findById(memberId.toString())
                .orElseThrow { ApplicationException(ApplicationExceptionType.JWT_REFRESH_NOT_FOUND_IN_REDIS) }

        // refresh token 검증
        if (!refreshTokenEntity.validateRefreshToken(refreshToken)) {
            throw ApplicationException(ApplicationExceptionType.JWT_REFRESH_INVALID)
        }

        // 토큰 재발급
        val tokens = jwtTokenProvider.generateTokens(memberId)

        // refresh token 저장
        saveRefreshToken(memberId, tokens.refreshToken)

        return tokens
    }

    /**
     * refresh token을 저장한다.
     */
    fun saveRefreshToken(
        memberId: Long,
        refreshToken: String,
    ) {
        refreshTokenRedisRepository.save(RefreshTokenEntity(memberId.toString(), refreshToken))
    }
}
