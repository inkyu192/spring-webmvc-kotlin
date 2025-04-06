package spring.webmvc.domain.repository

interface TokenRepository {
    fun findByMemberIdOrNull(memberId: Long): String?
    fun save(memberId: Long, token: String): String
}