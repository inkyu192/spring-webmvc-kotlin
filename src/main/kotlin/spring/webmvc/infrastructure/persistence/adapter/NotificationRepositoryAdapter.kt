package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.document.Notification
import spring.webmvc.domain.repository.NotificationRepository
import spring.webmvc.infrastructure.persistence.mongo.NotificationMongoRepository

@Component
class NotificationRepositoryAdapter(
    private val mongoRepository: NotificationMongoRepository
) : NotificationRepository {
    override fun save(notification: Notification) = mongoRepository.save(notification)
}