package spring.webmvc.application.strategy.email

interface EmailStrategy {
    fun emailTemplate(): EmailTemplate
    fun handle(payload: String)
}
