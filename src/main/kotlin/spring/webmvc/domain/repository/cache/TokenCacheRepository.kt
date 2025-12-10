package spring.webmvc.domain.repository.cache

interface TokenCacheRepository {
    fun setRefreshToken(userId: Long, refreshToken: String)
    fun getRefreshToken(userId: Long): String?
}