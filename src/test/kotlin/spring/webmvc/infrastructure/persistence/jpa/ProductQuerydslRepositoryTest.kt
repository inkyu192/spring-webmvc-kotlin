package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.infrastructure.config.RepositoryTest

@RepositoryTest
class ProductQuerydslRepositoryTest(
    @Autowired private val entityManager: EntityManager,
    @Autowired private val jpaQueryFactory: JPAQueryFactory,
) {
    private val productQuerydslRepository = ProductQuerydslRepository(jpaQueryFactory)

    private lateinit var product1: Accommodation
    private lateinit var product2: Accommodation
    private lateinit var product3: Accommodation
    private lateinit var product4: Accommodation

    @BeforeEach
    fun setUp() {
        val p1 = spring.webmvc.domain.model.entity.Product.create(
            category = spring.webmvc.domain.model.enums.Category.ACCOMMODATION,
            name = "product1",
            description = "description",
            price = 1000,
            quantity = 10
        )
        product1 = Accommodation.create(
            product = p1,
            place = "place1",
            checkInTime = java.time.Instant.now(),
            checkOutTime = java.time.Instant.now().plusSeconds(86400)
        )

        val p2 = spring.webmvc.domain.model.entity.Product.create(
            category = spring.webmvc.domain.model.enums.Category.ACCOMMODATION,
            name = "product2",
            description = "description",
            price = 2000,
            quantity = 20
        )
        product2 = Accommodation.create(
            product = p2,
            place = "place2",
            checkInTime = java.time.Instant.now(),
            checkOutTime = java.time.Instant.now().plusSeconds(86400)
        )

        val p3 = spring.webmvc.domain.model.entity.Product.create(
            category = spring.webmvc.domain.model.enums.Category.ACCOMMODATION,
            name = "product3",
            description = "description",
            price = 3000,
            quantity = 30
        )
        product3 = Accommodation.create(
            product = p3,
            place = "place3",
            checkInTime = java.time.Instant.now(),
            checkOutTime = java.time.Instant.now().plusSeconds(86400)
        )

        val p4 = spring.webmvc.domain.model.entity.Product.create(
            category = spring.webmvc.domain.model.enums.Category.ACCOMMODATION,
            name = "product4",
            description = "description",
            price = 1500,
            quantity = 30
        )
        product4 = Accommodation.create(
            product = p4,
            place = "place4",
            checkInTime = java.time.Instant.now(),
            checkOutTime = java.time.Instant.now().plusSeconds(86400)
        )

        entityManager.persist(p1)
        entityManager.persist(product1)
        entityManager.persist(p2)
        entityManager.persist(product2)
        entityManager.persist(p3)
        entityManager.persist(product3)
        entityManager.persist(p4)
        entityManager.persist(product4)

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    @DisplayName("findAllWithCursorPage: Product 조건 조회 후 반환한다")
    fun findAllWithCursorPage() {
        val query = ProductCursorPageQuery(
            cursorId = null,
            name = null,
            status = null,
        )

        val result = productQuerydslRepository.findAllWithCursorPage(query = query)

        Assertions.assertThat(result.content.size).isEqualTo(4)
        Assertions.assertThat(result.hasNext).isFalse
        Assertions.assertThat(result.nextCursorId).isNull()
    }
}