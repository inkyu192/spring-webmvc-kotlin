package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.UserCredential

interface UserCredentialJpaRepository : JpaRepository<UserCredential, Long> {
    fun findByEmailValue(email: String): UserCredential?
    fun existsByEmailValue(email: String): Boolean
}
