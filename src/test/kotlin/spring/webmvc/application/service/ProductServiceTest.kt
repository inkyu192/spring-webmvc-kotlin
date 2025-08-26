package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import spring.webmvc.application.dto.command.TicketCreateCommand
import spring.webmvc.application.dto.command.TicketUpdateCommand
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.application.strategy.ProductStrategy
import spring.webmvc.domain.model.cache.TicketCache
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.cache.ProductCacheRepository
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant

class ProductServiceTest : DescribeSpec({
    val productRepository = mockk<ProductRepository>()
    val productStrategyMap = mockk<Map<Category, ProductStrategy>>()
    val productStrategy = mockk<ProductStrategy>()
    val productCacheRepository = mockk<ProductCacheRepository>()
    val productService = ProductService(
        productCacheRepository = productCacheRepository,
        productRepository = productRepository,
        productStrategyMap = productStrategyMap,
    )

    lateinit var accommodation: Accommodation
    lateinit var flight: Flight
    lateinit var ticket: Ticket
    lateinit var products: List<Product>
    lateinit var cursorPage: CursorPage<Product>
    lateinit var ticketCache: TicketCache
    lateinit var ticketResult: TicketResult
    lateinit var ticketCreateCommand: TicketCreateCommand
    lateinit var ticketUpdateCommand: TicketUpdateCommand

    beforeEach {
        accommodation = spyk(
            Accommodation.create(
                name = "name1",
                description = "description",
                price = 1000,
                quantity = 10,
                place = "place1",
                checkInTime = Instant.now(),
                checkOutTime = Instant.now().plusSeconds(3600)
            )
        ).apply { every { id } returns 1L }

        flight = spyk(
            Flight.create(
                name = "name2",
                description = "description",
                price = 2000,
                quantity = 20,
                airline = "airline",
                flightNumber = "FL123",
                departureAirport = "ICN",
                arrivalAirport = "NRT",
                departureTime = Instant.now(),
                arrivalTime = Instant.now().plusSeconds(7200)
            )
        ).apply { every { id } returns 2L }

        ticket = spyk(
            Ticket.create(
                name = "name3",
                description = "description",
                price = 3000,
                quantity = 30,
                place = "place3",
                performanceTime = Instant.now(),
                duration = "2h",
                ageLimit = "All"
            )
        ).apply { every { id } returns 3L }

        products = listOf(accommodation, flight, ticket)

        cursorPage = CursorPage(
            content = products,
            size = 10,
            hasNext = false,
            nextCursorId = null
        )

        ticketCache = TicketCache(
            id = 1L,
            name = "name",
            description = "description",
            price = 1000,
            quantity = 10,
            createdAt = Instant.now(),
            ticketId = 1L,
            place = "place",
            performanceTime = Instant.now(),
            duration = "duration",
            ageLimit = "ageLimit"
        )

        ticketResult = TicketResult(ticketCache)

        ticketCreateCommand = mockk<TicketCreateCommand>()
        every { ticketCreateCommand.category } returns Category.TICKET

        ticketUpdateCommand = mockk<TicketUpdateCommand>()
        every { ticketUpdateCommand.category } returns Category.TICKET
    }

    describe("findProducts") {
        it(" Product 조회 후 반환한다") {
            val nextCursorId: Long? = null
            val size = 10
            val name = "name"

            every {
                productRepository.findWithCursorPage(
                    cursorId = nextCursorId,
                    size = size,
                    name = name
                )
            } returns cursorPage

            val result = productService.findProducts(cursorId = nextCursorId, size = size, name = name)

            result.content shouldHaveSize products.size
        }
    }

    describe("findProduct") {
        context("Product 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val productId = 1L
                val category = Category.TICKET

                every { productStrategyMap[category] } returns productStrategy
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

                every { productStrategyMap[category] } returns productStrategy
                every { productStrategy.findByProductId(productId) } returns ticketResult

                every { productCacheRepository.incrementProductViewCount(productId, 1) } returns 1

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

            every { productStrategyMap[category] } returns productStrategy
            every { productStrategy.createProduct(productCreateCommand = ticketCreateCommand) } returns ticketResult

            every { productCacheRepository.deleteProductStock(productId) } returns true

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

    describe("updateProduct") {
        it("수정 후 반환한다") {
            val productId = 1L
            val category = Category.TICKET

            every { productStrategyMap[category] } returns productStrategy
            every {
                productStrategy.updateProduct(
                    productId = productId,
                    productUpdateCommand = ticketUpdateCommand
                )
            } returns ticketResult

            every { productCacheRepository.deleteProductStock(productId) } returns true

            val result = productService.updateProduct(
                id = productId,
                productUpdateCommand = ticketUpdateCommand,
            )

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

    describe("deleteProduct") {
        it("Product 삭제한다") {
            val category = Category.TICKET
            val productId = 1L
            every { productStrategyMap[category] } returns productStrategy
            every { productStrategy.deleteProduct(productId) } returns Unit
            every { productCacheRepository.deleteProductStock(productId) } returns true

            productService.deleteProduct(category = category, id = productId)

            verify(exactly = 1) { productStrategy.deleteProduct(productId) }
        }
    }
})
