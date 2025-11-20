package spring.webmvc.application.service

import org.springframework.stereotype.Service
import spring.webmvc.application.event.NotificationEvent
import spring.webmvc.domain.model.document.Notification
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.NotificationRepository

@Service
class NotificationService(
    private val memberRepository: MemberRepository,
    private val notificationRepository: NotificationRepository,
) {
    fun createNotification(notificationEvent: NotificationEvent) {
        val member = memberRepository.findById(notificationEvent.memberId)

        notificationRepository.save(
            Notification.create(
                memberId = checkNotNull(member.id),
                title = notificationEvent.title,
                message = notificationEvent.message,
                url = notificationEvent.url,
            )
        )
    }
}