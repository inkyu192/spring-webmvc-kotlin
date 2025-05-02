package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.event.NotificationEvent
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Notification
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.NotificationRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class NotificationService(
    private val memberRepository: MemberRepository,
    private val notificationRepository: NotificationRepository,
) {
    @Transactional
    fun createNotification(notificationEvent: NotificationEvent) {
        val member = memberRepository.findByIdOrNull(notificationEvent.memberId)
            ?: throw EntityNotFoundException(kClass = Member::class, id = notificationEvent.memberId)

        notificationRepository.save(
            Notification.of(
                member = member,
                title = notificationEvent.title,
                message = notificationEvent.message,
                url = notificationEvent.url,
            )
        )
    }
}