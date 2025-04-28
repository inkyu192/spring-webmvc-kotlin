package spring.webmvc.application.event.listener

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener
import spring.webmvc.application.event.NotificationEvent
import spring.webmvc.application.service.NotificationService

@Component
class NotificationEventListener(
    private val notificationService: NotificationService,
) {
    @Async
    @TransactionalEventListener
    fun handleNotificationEvent(notificationEvent: NotificationEvent) {
        notificationService.createNotification(notificationEvent)
    }
}