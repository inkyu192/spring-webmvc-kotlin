package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.vo.Email

interface UserJpaRepository : JpaRepository<User, Long> {

    fun findByEmail(email: Email): User?

    fun existsByEmail(email: Email): Boolean
}
