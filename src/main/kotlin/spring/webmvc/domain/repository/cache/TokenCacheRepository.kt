package spring.webmvc.domain.repository.cache

interface TokenCacheRepository {
    fun setRefreshToken(userId: Long, deviceId: String, refreshToken: String)
    fun getRefreshToken(userId: Long, deviceId: String): String?
    fun removeRefreshToken(userId: Long, deviceId: String)
}
