package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.crypto.CryptoService

@DataJpaTest
class ProductQuerydslRepositoryTest(
    @Autowired private val entityManager: EntityManager,
) {
    @MockitoBean
    private val cryptoService = mock<CryptoService>()
    private val productQuerydslRepository = ProductQuerydslRepository(JPAQueryFactory(entityManager))

    private lateinit var product1: Product
    private lateinit var product2: Product
    private lateinit var product3: Product
    private lateinit var product4: Product

    @BeforeEach
    fun setUp() {
        product1 = Product.create(
            name = "product1",
            description = "description",
            price = 1000,
            quantity = 10,
            category = Category.TICKET
        )
        product2 = Product.create(
            name = "product2",
            description = "description",
            price = 2000,
            quantity = 20,
            category = Category.FLIGHT
        )
        product3 = Product.create(
            name = "product3",
            price = 3000,
            quantity = 30,
            category = Category.TICKET,
            description = "description",
        )
        product4 = Product.create(
            name = "product4",
            description = "description",
            price = 1500,
            quantity = 30,
            category = Category.FLIGHT
        )
        entityManager.persist(product1)
        entityManager.persist(product2)
        entityManager.persist(product3)
        entityManager.persist(product4)

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    @DisplayName("findAll: Product 조건 조회 후 반환한다")
    fun findAll() {
        val nextCursorId = null
        val size = 3
        val name = null

        val result = productQuerydslRepository.findAll(nextCursorId = nextCursorId, size = size, name = name)

        assertThat(result.content.size).isEqualTo(size)
        assertThat(result.size).isEqualTo(size)
        assertThat(result.hasNext).isTrue
        assertThat(result.nextCursorId).isEqualTo(product1.id)
    }
}