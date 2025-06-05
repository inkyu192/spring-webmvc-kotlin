package spring.webmvc.domain.repository

import spring.webmvc.domain.model.document.Notification

interface NotificationRepository {
    fun save(notification: Notification): Notification
}