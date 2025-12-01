package spring.webmvc.application.enums

enum class EmailTemplate(
    val subject: String,
    val templatePath: String,
) {
    JOIN_VERIFY(
        subject = "회원가입 인증",
        templatePath = "email/join-verify",
    ),
    PASSWORD_RESET(
        subject = "비밀번호 재설정",
        templatePath = "email/password-reset",
    ),
}
