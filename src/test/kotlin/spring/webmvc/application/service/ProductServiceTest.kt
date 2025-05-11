package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spring.webmvc.application.dto.command.TicketCreateCommand
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.application.strategy.ProductStrategy
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant

class ProductServiceTest : DescribeSpec({
    val productRepository = mockk<ProductRepository>()
    val productStrategy = mockk<ProductStrategy>()
    val productService = ProductService(
        productRepository = productRepository,
        productStrategies = listOf(productStrategy)
    )

    describe("findProducts") {
        it(" Product 조회 후 반환한다") {
            val pageable = PageRequest.of(0, 10)
            val name = "name"
            val products = listOf(
                spyk(
                    Product.create(
                        name = "name1",
                        description = "description",
                        price = 1000,
                        quantity = 10,
                        category = Category.ACCOMMODATION
                    )
                ).apply { every { id } returns 1L },
                spyk(
                    Product.create(
                        name = "name2",
                        description = "description",
                        price = 2000,
                        quantity = 20,
                        category = Category.FLIGHT
                    )
                ).apply { every { id } returns 2L },
                spyk(
                    Product.create(
                        name = "name3",
                        description = "description",
                        price = 3000,
                        quantity = 30,
                        category = Category.TICKET
                    )
                ).apply { every { id } returns 3L },
            )
            val page = PageImpl(products, pageable, products.size.toLong())

            every { productRepository.findAll(pageable = pageable, name = name) } returns page

            val result = productService.findProducts(pageable = pageable, name = name)

            result.content shouldHaveSize products.size
        }
    }

    describe("findProduct") {
        context("Product 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val productId = 1L
                val category = Category.TICKET

                every { productStrategy.supports(category) } returns true
                every { productStrategy.findByProductId(productId) } throws EntityNotFoundException(
                    kClass = Ticket::class,
                    id = productId
                )

                shouldThrow<EntityNotFoundException> { productService.findProduct(id = productId, category = category) }
            }
        }
        context("Product 있을 경우") {
            it("조회 후 반환한다") {
                val productId = 1L
                val category = Category.TICKET
                val ticketResult = TicketResult(
                    id = productId,
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 10, createdAt = Instant.now(),
                    ticketId = 1L,
                    place = "place",
                    performanceTime = Instant.now(),
                    duration = "duration",
                    ageLimit = "ageLimit"
                )

                every { productStrategy.supports(category) } returns true
                every { productStrategy.findByProductId(productId) } returns ticketResult

                val result = productService.findProduct(id = productId, category = category)

                result.name shouldBe ticketResult.name
                result.description shouldBe ticketResult.description
                result.price shouldBe ticketResult.price
                result.quantity shouldBe ticketResult.quantity

                val ticket = result.shouldBeInstanceOf<TicketResult>()
                ticket.place shouldBe ticketResult.place
                ticket.performanceTime shouldBe ticketResult.performanceTime
                ticket.duration shouldBe ticketResult.duration
                ticket.ageLimit shouldBe ticketResult.ageLimit
            }
        }
    }

    describe("createProduct") {
        it("Product 저장 후 반환한다") {
            val productId = 1L
            val category = Category.TICKET

            val ticketCreateCommand = mockk<TicketCreateCommand>()
            every { ticketCreateCommand.category } returns category

            val ticketResult = TicketResult(
                id = productId,
                name = "name",
                description = "description",
                price = 1000,
                quantity = 10, createdAt = Instant.now(),
                ticketId = 1L,
                place = "place",
                performanceTime = Instant.now(),
                duration = "duration",
                ageLimit = "ageLimit"
            )

            every { productStrategy.supports(category) } returns true
            every { productStrategy.createProduct(productCreateCommand = ticketCreateCommand) } returns ticketResult

            val result = productService.createProduct(ticketCreateCommand)

            result.shouldBeInstanceOf<TicketResult>().apply {
                name shouldBe ticketResult.name
                description shouldBe ticketResult.description
                price shouldBe ticketResult.price
                quantity shouldBe ticketResult.quantity
                place shouldBe ticketResult.place
                performanceTime shouldBe ticketResult.performanceTime
                duration shouldBe ticketResult.duration
                ageLimit shouldBe ticketResult.ageLimit
            }
        }
    }
})
