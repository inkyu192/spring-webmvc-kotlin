package spring.webmvc.infrastructure.config.sentry

import io.sentry.EventProcessor
import io.sentry.Hint
import io.sentry.SentryEvent
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Component
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@Component
class FilterEventProcessor : EventProcessor {

    override fun process(event: SentryEvent, hint: Hint): SentryEvent? {
        return when (event.throwable) {
            is MethodArgumentNotValidException,
            is HttpMessageNotReadableException,
            is MethodArgumentTypeMismatchException,
            is MethodArgumentConversionNotSupportedException,
            is NoResourceFoundException,
            is HttpRequestMethodNotSupportedException,
            is ServletRequestBindingException -> null

            else -> event
        }
    }
}
