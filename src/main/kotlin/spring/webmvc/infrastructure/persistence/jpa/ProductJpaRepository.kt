package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Product

interface ProductJpaRepository : JpaRepository<Product, Long> {
}