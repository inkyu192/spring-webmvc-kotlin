package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.vo.Email

interface UserCredentialRepository {
    fun findByEmail(email: Email): UserCredential?
    fun findByUser(user: User): UserCredential?
    fun existsByEmail(email: Email): Boolean
    fun save(userCredential: UserCredential): UserCredential
}