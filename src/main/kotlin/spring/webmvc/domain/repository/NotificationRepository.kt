package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Notification

interface NotificationRepository {
    fun save(notification: Notification): Notification
}