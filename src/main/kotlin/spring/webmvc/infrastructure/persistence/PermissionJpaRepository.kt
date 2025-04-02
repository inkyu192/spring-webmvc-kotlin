package spring.webmvc.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Permission

interface PermissionJpaRepository : JpaRepository<Permission, Long> {
}