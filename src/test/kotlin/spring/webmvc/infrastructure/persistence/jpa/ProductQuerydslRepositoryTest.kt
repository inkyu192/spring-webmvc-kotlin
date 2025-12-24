package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
        product1 = Accommodation.create(
            name = "product1",
            description = "description",
            price = 1000,
            quantity = 10,
            place = "place1",
            checkInTime = java.time.Instant.now(),
            checkOutTime = java.time.Instant.now().plusSeconds(86400)
        )
        product2 = Accommodation.create(
            name = "product2",
            description = "description",
            price = 2000,
            quantity = 20,
            place = "place2",
            checkInTime = java.time.Instant.now(),
            checkOutTime = java.time.Instant.now().plusSeconds(86400)
        )
        product3 = Accommodation.create(
            name = "product3",
            price = 3000,
            quantity = 30,
            description = "description",
            place = "place3",
            checkInTime = java.time.Instant.now(),
            checkOutTime = java.time.Instant.now().plusSeconds(86400)
        )
        product4 = Accommodation.create(
            name = "product4",
            description = "description",
            price = 1500,
            quantity = 30,
            place = "place4",
            checkInTime = java.time.Instant.now(),
            checkOutTime = java.time.Instant.now().plusSeconds(86400)
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

        val result = productQuerydslRepository.findAll(cursorId = nextCursorId, size = size, name = name)

        Assertions.assertThat(result.content.size).isEqualTo(size)
        Assertions.assertThat(result.size).isEqualTo(size)
        Assertions.assertThat(result.hasNext).isTrue
        Assertions.assertThat(result.nextCursorId).isEqualTo(product1.id)
    }
}