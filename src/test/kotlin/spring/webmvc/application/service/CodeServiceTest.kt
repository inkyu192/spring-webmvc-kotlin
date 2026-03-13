package spring.webmvc.application.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

class CodeServiceTest {
    private val translationService = mockk<TranslationService>()
    private val codeService = CodeService(translationService)

    private val locale = Locale.KOREAN

    @BeforeEach
    fun setUp() {
        LocaleContextHolder.setLocale(locale)
    }

    @AfterEach
    fun tearDown() {
        LocaleContextHolder.resetLocaleContext()
    }

    @Test
    @DisplayName("전체 코드 그룹 및 하위 코드 조회")
    fun findCodes() {
        every { translationService.getMessage("Gender", locale) } returns "성별"
        every { translationService.getMessage("Gender.MALE", locale) } returns "남성"
        every { translationService.getMessage("Gender.FEMALE", locale) } returns "여성"
        every { translationService.getMessage("CurationCategory", locale) } returns "큐레이션 카테고리"
        every {
            translationService.getMessage(
                match { it.startsWith("CurationCategory.") },
                eq(locale)
            )
        } returns "큐레이션"
        every { translationService.getMessage("OauthProvider", locale) } returns "OAuth 제공자"
        every {
            translationService.getMessage(
                match { it.startsWith("OauthProvider.") },
                eq(locale)
            )
        } returns "OAuth"
        every { translationService.getMessage("OrderStatus", locale) } returns "주문 상태"
        every {
            translationService.getMessage(
                match { it.startsWith("OrderStatus.") },
                eq(locale)
            )
        } returns "주문"
        every { translationService.getMessage("ProductCategory", locale) } returns "상품 카테고리"
        every {
            translationService.getMessage(
                match { it.startsWith("ProductCategory.") },
                eq(locale)
            )
        } returns "카테고리"
        every { translationService.getMessage("ProductStatus", locale) } returns "상품 상태"
        every {
            translationService.getMessage(
                match { it.startsWith("ProductStatus.") },
                eq(locale)
            )
        } returns "상태"

        val result = codeService.findCodes()

        assertThat(result).hasSize(6)
        assertThat(result.map { it.name }).containsExactlyInAnyOrder(
            "CurationCategory", "Gender", "OauthProvider", "OrderStatus", "ProductCategory", "ProductStatus"
        )

        val genderGroup = result.first { it.name == "Gender" }
        assertThat(genderGroup.label).isEqualTo("성별")
        assertThat(genderGroup.codes).hasSize(2)
        assertThat(genderGroup.codes[0].code).isEqualTo("MALE")
        assertThat(genderGroup.codes[0].label).isEqualTo("남성")
        assertThat(genderGroup.codes[1].code).isEqualTo("FEMALE")
        assertThat(genderGroup.codes[1].label).isEqualTo("여성")
    }
}
