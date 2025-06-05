package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Notification

interface NotificationJpaRepository: JpaRepository<Notification, Long> {
}