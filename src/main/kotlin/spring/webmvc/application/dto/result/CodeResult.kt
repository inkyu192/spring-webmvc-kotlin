package spring.webmvc.application.dto.result

data class CodeGroupResult(
    val name: String,
    val label: String,
    val codes: List<CodeResult>,
)

data class CodeResult(
    val code: String,
    val label: String,
)
