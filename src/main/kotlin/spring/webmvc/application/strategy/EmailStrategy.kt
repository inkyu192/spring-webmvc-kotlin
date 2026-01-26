package spring.webmvc.application.strategy

import spring.webmvc.application.enums.EmailTemplate

interface EmailStrategy {
    fun emailTemplate(): EmailTemplate
    fun handle(payload: String)
}
