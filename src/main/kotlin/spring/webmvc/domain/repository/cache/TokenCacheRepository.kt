package spring.webmvc.domain.repository.cache

interface TokenCacheRepository {
    fun addRefreshToken(userId: Long, refreshToken: String)
    fun getRefreshToken(userId: Long, refreshToken: String): String?
    fun removeRefreshToken(userId: Long, refreshToken: String)
}
