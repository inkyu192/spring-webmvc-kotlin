package spring.webmvc.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Role

interface RoleJpaRepository: JpaRepository<Role, Long> {
}