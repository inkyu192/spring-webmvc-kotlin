package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.vo.Phone

interface UserJpaRepository : JpaRepository<User, Long> {
    fun existsByPhone(phone: Phone): Boolean
}
