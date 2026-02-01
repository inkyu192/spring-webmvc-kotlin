package spring.webmvc.infrastructure.external.notification

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import spring.webmvc.infrastructure.properties.AppProperties

@Component
class NotificationClient(
    appProperties: AppProperties,
) {
    private val restClient: RestClient = RestClient.builder()
        .baseUrl(appProperties.external.notification.host)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun sendVerifyEmail(request: VerifyEmailRequest) {
        restClient.post()
            .uri("/email/verify")
            .body(request)
            .retrieve()
            .toBodilessEntity()
    }

    fun sendPasswordResetEmail(request: PasswordResetEmailRequest) {
        restClient.post()
            .uri("/email/password-reset")
            .body(request)
            .retrieve()
            .toBodilessEntity()
    }
}
