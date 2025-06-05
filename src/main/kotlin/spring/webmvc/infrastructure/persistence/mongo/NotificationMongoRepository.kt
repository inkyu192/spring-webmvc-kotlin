package spring.webmvc.infrastructure.persistence.mongo

import org.springframework.data.mongodb.repository.MongoRepository
import spring.webmvc.domain.model.document.Notification

interface NotificationMongoRepository : MongoRepository<Notification, String> {
}