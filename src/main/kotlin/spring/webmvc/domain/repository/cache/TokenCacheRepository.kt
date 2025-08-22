package spring.webmvc.domain.repository.cache

interface TokenCacheRepository {
    fun setRefreshToken(memberId: Long, refreshToken: String)
    fun getRefreshToken(memberId: Long): String?
}