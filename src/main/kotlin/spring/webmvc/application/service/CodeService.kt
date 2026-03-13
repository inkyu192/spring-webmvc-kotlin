package spring.webmvc.application.service

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.stereotype.Service
import spring.webmvc.application.dto.result.CodeGroupResult
import spring.webmvc.application.dto.result.CodeResult

@Service
class CodeService(
    private val translationService: TranslationService,
) {
    private val codeRegistry: Map<String, List<Enum<*>>>

    init {
        val provider = ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(AssignableTypeFilter(Enum::class.java))

        codeRegistry = provider.findCandidateComponents("spring.webmvc.domain.model.enums")
            .mapNotNull { beanDef ->
                val clazz = Class.forName(beanDef.beanClassName)
                if (clazz.isEnum) {
                    clazz.simpleName to clazz.enumConstants.filterIsInstance<Enum<*>>()
                } else null
            }
            .toMap()
    }

    fun findCodes(): List<CodeGroupResult> {
        val locale = LocaleContextHolder.getLocale()

        return codeRegistry.map { (groupName, codes) ->
            CodeGroupResult(
                name = groupName,
                label = translationService.getMessage(code = groupName, locale = locale),
                codes = codes.map { code ->
                    CodeResult(
                        code = code.name,
                        label = translationService.getMessage(
                            code = "${groupName}.${code.name}",
                            locale = locale,
                        ),
                    )
                },
            )
        }
    }
}
