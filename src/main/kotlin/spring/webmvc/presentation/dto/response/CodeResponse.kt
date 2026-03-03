package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.CodeGroupResult
import spring.webmvc.application.dto.result.CodeResult

data class CodeListResponse(
    val size: Long,
    val codeGroups: List<CodeGroupResponse>,
) {
    companion object {
        fun of(results: List<CodeGroupResult>) = CodeListResponse(
            size = results.size.toLong(),
            codeGroups = results.map { CodeGroupResponse.of(it) },
        )
    }
}

data class CodeGroupResponse(
    val name: String,
    val label: String,
    val codes: List<CodeResponse>,
) {
    companion object {
        fun of(result: CodeGroupResult) = CodeGroupResponse(
            name = result.name,
            label = result.label,
            codes = result.codes.map { CodeResponse.of(it) },
        )
    }
}

data class CodeResponse(
    val code: String,
    val label: String,
) {
    companion object {
        fun of(result: CodeResult) = CodeResponse(
            code = result.code,
            label = result.label,
        )
    }
}
