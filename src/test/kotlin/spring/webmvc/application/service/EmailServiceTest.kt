package spring.webmvc.application.service

import io.mockk.*
import jakarta.mail.internet.MimeMessage
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import spring.webmvc.application.event.SendPasswordResetEmailEvent
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.cache.AuthCacheRepository

class EmailServiceTest {
    private val mailSender = mockk<JavaMailSender>()
    private val templateEngine = mockk<TemplateEngine>()
    private val authCacheRepository = mockk<AuthCacheRepository>()
    private val emailService = EmailService(
        mailSender = mailSender,
        templateEngine = templateEngine,
        authCacheRepository = authCacheRepository,
    )

    private lateinit var email: Email
    private lateinit var mimeMessage: MimeMessage

    @BeforeEach
    fun setUp() {
        email = Email.create("test@example.com")
        mimeMessage = mockk<MimeMessage>(relaxed = true)
    }

    @Test
    @DisplayName("회원가입 인증 이메일 발송")
    fun sendVerifyEmail() {
        val event = SendVerifyEmailEvent(email = email)
        val tokenSlot = slot<String>()

        every { authCacheRepository.setJoinVerifyToken(capture(tokenSlot), email) } just runs
        every { templateEngine.process(any<String>(), any<Context>()) } returns "<html>Verify Email</html>"
        every { mailSender.createMimeMessage() } returns mimeMessage
        every { mailSender.send(any<MimeMessage>()) } just runs

        emailService.sendVerifyEmail(event)

        val capturedToken = tokenSlot.captured
        Assertions.assertThat(capturedToken).isNotNull()
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 발송")
    fun sendPasswordResetEmail() {
        val event = SendPasswordResetEmailEvent(email = email)
        val tokenSlot = slot<String>()

        every { authCacheRepository.setPasswordResetToken(capture(tokenSlot), email) } just runs
        every { templateEngine.process(any<String>(), any<Context>()) } returns "<html>Reset Password</html>"
        every { mailSender.createMimeMessage() } returns mimeMessage
        every { mailSender.send(any<MimeMessage>()) } just runs

        emailService.sendPasswordResetEmail(event)

        val capturedToken = tokenSlot.captured
        Assertions.assertThat(capturedToken).isNotNull()
    }
}
