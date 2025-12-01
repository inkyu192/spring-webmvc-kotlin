package spring.webmvc.domain.repository.cache

import spring.webmvc.domain.model.vo.Email

interface AuthCacheRepository {
    fun setJoinVerifyToken(token: String, email: Email)
    fun getJoinVerifyToken(token: String): String?
    fun deleteJoinVerifyToken(token: String)
    fun setPasswordResetToken(token: String, email: Email)
    fun getPasswordResetToken(token: String): String?
    fun deletePasswordResetToken(token: String)
}