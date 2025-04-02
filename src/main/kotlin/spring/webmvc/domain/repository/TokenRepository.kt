package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Token

interface TokenRepository {
    fun findByIdOrNull(id: Long): Token?
    fun save(token: Token): Token
}