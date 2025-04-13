package spring.webmvc.application.event

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Notification
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.NotificationRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
@Transactional(readOnly = true)
class NotificationEventListener(
    private val memberRepository: MemberRepository,
    private val notificationRepository: NotificationRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleNotificationEvent(notificationEvent: NotificationEvent) {
        runCatching {
            Thread.sleep(3000)

            val member = memberRepository.findByIdOrNull(notificationEvent.memberId)
                ?: throw EntityNotFoundException(clazz = Member::class.java, id = notificationEvent.memberId)

            notificationRepository.save(
                Notification.of(
                    member = member,
                    title = notificationEvent.title,
                    message = notificationEvent.message,
                    url = notificationEvent.url
                )
            )
        }.onFailure { e -> log.error("알림 이벤트 처리 중 예외 발생: {}", e.message, e) }
    }
}