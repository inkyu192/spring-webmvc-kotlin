package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.infrastructure.config.RepositoryTest

@RepositoryTest
class ProductQuerydslRepositoryTest(
    @Autowired private val entityManager: EntityManager,
) {
    private val productQuerydslRepository = ProductQuerydslRepository(JPAQueryFactory(entityManager))

    private lateinit var product1: Ticket
    private lateinit var product2: Ticket
    private lateinit var product3: Ticket
    private lateinit var product4: Ticket

    @BeforeEach
    fun setUp() {
        product1 = Ticket.create(
            name = "product1",
            description = "description",
            price = 1000,
            quantity = 10,
            place = "place1",
            performanceTime = java.time.Instant.now(),
            duration = "2h",
            ageLimit = "12+"
        )
        product2 = Ticket.create(
            name = "product2",
            description = "description",
            price = 2000,
            quantity = 20,
            place = "place2",
            performanceTime = java.time.Instant.now(),
            duration = "3h",
            ageLimit = "15+"
        )
        product3 = Ticket.create(
            name = "product3",
            price = 3000,
            quantity = 30,
            description = "description",
            place = "place3",
            performanceTime = java.time.Instant.now(),
            duration = "1h",
            ageLimit = "All"
        )
        product4 = Ticket.create(
            name = "product4",
            description = "description",
            price = 1500,
            quantity = 30,
            place = "place4",
            performanceTime = java.time.Instant.now(),
            duration = "2h",
            ageLimit = "18+"
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