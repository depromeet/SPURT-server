package com.ssak3.timeattack.common.security.refreshtoken

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRedisRepository : CrudRepository<RefreshTokenEntity, String>
