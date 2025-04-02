package spring.webmvc.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Item

interface ItemJpaRepository : JpaRepository<Item, Long>