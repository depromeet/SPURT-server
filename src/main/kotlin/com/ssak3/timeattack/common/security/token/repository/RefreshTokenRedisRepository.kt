package com.ssak3.timeattack.common.security.token.repository

import com.ssak3.timeattack.common.security.token.domain.RefreshToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRedisRepository : CrudRepository<RefreshToken, String> {

}
