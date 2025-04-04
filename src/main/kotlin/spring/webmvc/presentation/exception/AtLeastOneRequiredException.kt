package spring.webmvc.presentation.exception

class AtLeastOneRequiredException(vararg fields: String) : AbstractValidationException("적어도 하나는 필수 입력값입니다.") {
    val fields: List<String> = fields.toList()
}