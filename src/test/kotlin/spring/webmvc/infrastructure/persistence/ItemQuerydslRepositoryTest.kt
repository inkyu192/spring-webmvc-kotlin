package spring.webmvc.infrastructure.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.Item
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.config.DataJpaTestConfig

@DataJpaTest
@Import(DataJpaTestConfig::class)
class ItemQuerydslRepositoryTest(
    jpaQueryFactory: JPAQueryFactory,
    private val itemJpaRepository: ItemJpaRepository,
) : DescribeSpec({
    val itemQuerydslRepository = ItemQuerydslRepository(jpaQueryFactory)

    describe("findAll 은") {
        it("name 을 필털링하고 페이징 되어서 조회한다") {
            val request = listOf(
                Item.create(
                    name = "이름1",
                    description = "설명1",
                    price = 150,
                    quantity = 1,
                    category = Category.ROLE_BOOK
                ),
                Item.create(
                    name = "이름2",
                    description = "설명2",
                    price = 160,
                    quantity = 2,
                    category = Category.ROLE_BOOK
                ),
                Item.create(
                    name = "이름3",
                    description = "설명3",
                    price = 170,
                    quantity = 3,
                    category = Category.ROLE_BOOK
                ),
                Item.create(
                    name = "이름4",
                    description = "설명4",
                    price = 180,
                    quantity = 4,
                    category = Category.ROLE_TICKET
                ),
                Item.create(
                    name = "이5",
                    description = "설명5",
                    price = 190,
                    quantity = 5,
                    category = Category.ROLE_TICKET
                )
            )
            itemJpaRepository.saveAll(request)

            val pageable: Pageable = PageRequest.of(0, 3)
            val name = "이름"

            itemQuerydslRepository.findAll(pageable = pageable, name = name).apply {
                number shouldBe pageable.pageNumber
                size shouldBe pageable.pageSize
                totalElements shouldBe request.filter { it.name.contains(name) }.size.toLong()
            }
        }
    }
})
