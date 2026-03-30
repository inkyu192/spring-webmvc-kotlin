package spring.webmvc.infrastructure.config.sentry

import io.jsonwebtoken.JwtException
import io.sentry.EventProcessor
import io.sentry.Hint
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.exception.*

@Component
class LevelEventProcessor : EventProcessor {

    override fun process(event: SentryEvent, hint: Hint): SentryEvent? {
        event.level = when (event.throwable) {
            is NotFoundEntityException,
            is DuplicateEntityException,
            is DuplicateRequestException,
            is InvalidEntityStatusException,
            is InsufficientQuantityException,
            is InvalidCredentialsException,
            is AccessDeniedException,
            is JwtException -> SentryLevel.WARNING

            else -> SentryLevel.ERROR
        }

        return event
    }
}
