package spring.webmvc.domain.repository

interface TokenCacheRepository {
    fun setRefreshToken(memberId: Long, refreshToken: String)
    fun getRefreshToken(memberId: Long): String?
}