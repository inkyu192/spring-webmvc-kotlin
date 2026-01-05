package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.vo.Email

interface UserCredentialRepository {
    fun findByEmail(email: Email): UserCredential?
    fun findByUserId(userId: Long): UserCredential?
    fun existsByEmail(email: Email): Boolean
    fun save(userCredential: UserCredential): UserCredential
}