package spring.webmvc.application.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.i18n.LocaleContextHolder
import spring.webmvc.domain.repository.cache.TranslationCacheRepository
import java.util.*

class CodeServiceTest {
    private val translationCacheRepository = mockk<TranslationCacheRepository>()
    private val codeService = CodeService(translationCacheRepository)

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
        every { translationCacheRepository.getMessage("Gender", locale) } returns "성별"
        every { translationCacheRepository.getMessage("Gender.MALE", locale) } returns "남성"
        every { translationCacheRepository.getMessage("Gender.FEMALE", locale) } returns "여성"
        every { translationCacheRepository.getMessage("CurationCategory", locale) } returns "큐레이션 카테고리"
        every {
            translationCacheRepository.getMessage(
                match { it.startsWith("CurationCategory.") },
                eq(locale)
            )
        } returns "큐레이션"
        every { translationCacheRepository.getMessage("OauthProvider", locale) } returns "OAuth 제공자"
        every {
            translationCacheRepository.getMessage(
                match { it.startsWith("OauthProvider.") },
                eq(locale)
            )
        } returns "OAuth"
        every { translationCacheRepository.getMessage("OrderStatus", locale) } returns "주문 상태"
        every {
            translationCacheRepository.getMessage(
                match { it.startsWith("OrderStatus.") },
                eq(locale)
            )
        } returns "주문"
        every { translationCacheRepository.getMessage("ProductCategory", locale) } returns "상품 카테고리"
        every {
            translationCacheRepository.getMessage(
                match { it.startsWith("ProductCategory.") },
                eq(locale)
            )
        } returns "카테고리"
        every { translationCacheRepository.getMessage("ProductStatus", locale) } returns "상품 상태"
        every {
            translationCacheRepository.getMessage(
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
