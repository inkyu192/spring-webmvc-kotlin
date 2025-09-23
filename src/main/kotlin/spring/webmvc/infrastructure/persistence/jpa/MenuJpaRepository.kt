package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Menu

interface MenuJpaRepository : JpaRepository<Menu, Long>