package spring.webmvc.presentation.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spring.webmvc.application.service.CodeService
import spring.webmvc.presentation.dto.response.CodeListResponse

@RestController
@RequestMapping("/codes")
class CodeController(
    private val codeService: CodeService,
) {
    @GetMapping
    fun findCodes() = codeService.findCodes().let { CodeListResponse.of(results = it) }
}
