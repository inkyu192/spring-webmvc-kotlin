package spring.webmvc.infrastructure.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.config.DataJpaTestConfig

@DataJpaTest
@Import(DataJpaTestConfig::class)
class ProductQuerydslRepositoryTest(
    jpaQueryFactory: JPAQueryFactory,
    private val productJpaRepository: ProductJpaRepository,
) : DescribeSpec({
    val productQuerydslRepository = ProductQuerydslRepository(jpaQueryFactory)

    describe("findAll") {
        it("Product 조건 조회 후 반환한다") {
            val request = listOf(
                Product.create(
                    name = "product1",
                    description = "description",
                    price = 1000,
                    quantity = 10,
                    category = Category.TICKET
                ),
                Product.create(
                    name = "product2",
                    description = "description",
                    price = 2000,
                    quantity = 20,
                    category = Category.FLIGHT
                ),
                Product.create(
                    name = "product3",
                    description = "description",
                    price = 3000,
                    quantity = 30,
                    category = Category.TICKET
                ),
                Product.create(
                    name = "product4",
                    description = "description",
                    price = 1500,
                    quantity = 30,
                    category = Category.FLIGHT
                ),
                Product.create(
                    name = "fake",
                    description = "description",
                    price = 2500,
                    quantity = 30,
                    category = Category.ACCOMMODATION
                )
            )

            productJpaRepository.saveAll(request)

            val pageable: Pageable = PageRequest.of(0, 3)
            val name = "이름"

            productQuerydslRepository.findAll(pageable = pageable, name = name).apply {
                number shouldBe pageable.pageNumber
                size shouldBe pageable.pageSize
                totalElements shouldBe request.filter { it.name.contains(name) }.size.toLong()
            }
        }
    }
})
