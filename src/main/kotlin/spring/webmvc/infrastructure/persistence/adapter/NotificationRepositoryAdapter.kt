package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Notification
import spring.webmvc.domain.repository.NotificationRepository
import spring.webmvc.infrastructure.persistence.jpa.NotificationJpaRepository

@Component
class NotificationRepositoryAdapter(
    private val jpaRepository: NotificationJpaRepository
) : NotificationRepository {
    override fun save(notification: Notification) = jpaRepository.save(notification)
}