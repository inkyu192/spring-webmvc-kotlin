package spring.webmvc.application.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import spring.webmvc.domain.model.entity.Menu
import spring.webmvc.domain.repository.MenuRepository
import java.util.*

class MenuServiceTest {
    private val menuRepository = mockk<MenuRepository>()
    private val translationService = mockk<TranslationService>()
    private val menuService = MenuService(menuRepository, translationService)

    private val locale = Locale.KOREAN

    @BeforeEach
    fun setUp() {
        LocaleContextHolder.setLocale(locale)

        val authorities = listOf(
            SimpleGrantedAuthority("PRODUCT_READ"),
            SimpleGrantedAuthority("PRODUCT_WRITE"),
        )
        val authentication = UsernamePasswordAuthenticationToken("1", null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @AfterEach
    fun tearDown() {
        LocaleContextHolder.resetLocaleContext()
        SecurityContextHolder.clearContext()
    }

    @Test
    @DisplayName("메뉴 계층 구조 및 locale별 번역 정상 동작")
    fun findMenus() {
        val parentMenu = Menu.create(translationCode = "menu.products", sortOrder = 2).apply {
            val idField = Menu::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(this, 2L)
        }

        val childMenu1 = Menu.create(
            translationCode = "menu.products.transports",
            path = "/products/transports",
            parent = parentMenu,
            sortOrder = 1,
        ).apply {
            val idField = Menu::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(this, 6L)
        }

        val childMenu2 = Menu.create(
            translationCode = "menu.products.accommodations",
            path = "/products/accommodations",
            parent = parentMenu,
            sortOrder = 2,
        ).apply {
            val idField = Menu::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(this, 7L)
        }

        every {
            menuRepository.findAllWithRecursiveByPermissions(setOf("PRODUCT_READ", "PRODUCT_WRITE"))
        } returns listOf(parentMenu, childMenu1, childMenu2)

        every { translationService.getMessage("menu.products", locale) } returns "상품"
        every { translationService.getMessage("menu.products.transports", locale) } returns "교통수단관리"
        every { translationService.getMessage("menu.products.accommodations", locale) } returns "숙박관리"

        val result = menuService.findMenus()

        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(2L)
        assertThat(result[0].name).isEqualTo("상품")
        assertThat(result[0].path).isNull()
        assertThat(result[0].children).hasSize(2)
        assertThat(result[0].children[0].id).isEqualTo(6L)
        assertThat(result[0].children[0].name).isEqualTo("교통수단관리")
        assertThat(result[0].children[0].path).isEqualTo("/products/transports")
        assertThat(result[0].children[1].id).isEqualTo(7L)
        assertThat(result[0].children[1].name).isEqualTo("숙박관리")
        assertThat(result[0].children[1].path).isEqualTo("/products/accommodations")
    }

    @Test
    @DisplayName("영어 locale로 메뉴 조회 시 영어 번역 반환")
    fun findMenusEnglish() {
        val enLocale = Locale.ENGLISH
        LocaleContextHolder.setLocale(enLocale)

        val parentMenu = Menu.create(translationCode = "menu.products", sortOrder = 2).apply {
            val idField = Menu::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(this, 2L)
        }

        val childMenu = Menu.create(
            translationCode = "menu.products.transports",
            path = "/products/transports",
            parent = parentMenu,
            sortOrder = 1,
        ).apply {
            val idField = Menu::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(this, 6L)
        }

        every {
            menuRepository.findAllWithRecursiveByPermissions(setOf("PRODUCT_READ", "PRODUCT_WRITE"))
        } returns listOf(parentMenu, childMenu)

        every { translationService.getMessage("menu.products", enLocale) } returns "Products"
        every { translationService.getMessage("menu.products.transports", enLocale) } returns "Transport Management"

        val result = menuService.findMenus()

        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("Products")
        assertThat(result[0].children).hasSize(1)
        assertThat(result[0].children[0].name).isEqualTo("Transport Management")
    }

    @Test
    @DisplayName("권한이 없으면 빈 리스트 반환")
    fun findMenusEmptyPermissions() {
        val authentication = UsernamePasswordAuthenticationToken("1", null, emptyList())
        SecurityContextHolder.getContext().authentication = authentication

        val result = menuService.findMenus()

        assertThat(result).isEmpty()
    }
}
