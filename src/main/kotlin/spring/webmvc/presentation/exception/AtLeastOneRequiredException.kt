package spring.webmvc.presentation.exception

class AtLeastOneRequiredException(
    vararg fields: String
) : AbstractValidationException(
    message = "적어도 하나는 필수 입력값입니다.",
    fields = fields,
)