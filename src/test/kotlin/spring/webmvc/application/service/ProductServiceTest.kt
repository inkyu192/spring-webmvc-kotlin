package spring.webmvc.application.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.ProductRepository

class ProductServiceTest : DescribeSpec({

    val productRepository = mockk<ProductRepository>()
    val productService = ProductService(productRepository)

    describe("findProducts") {
        it(" Product 조회 후 반환한다") {
            val pageable = PageRequest.of(0, 10)
            val name = "name"
            val products = listOf(
                Product.create(
                    name = "name1",
                    description = "description",
                    price = 1000,
                    quantity = 10,
                    category = Category.ACCOMMODATION
                ),
                Product.create(
                    name = "name2",
                    description = "description",
                    price = 2000,
                    quantity = 20,
                    category = Category.FLIGHT
                ),
                Product.create(
                    name = "name3",
                    description = "description",
                    price = 3000,
                    quantity = 30,
                    category = Category.TICKET
                ),
            )
            val page = PageImpl(products, pageable, products.size.toLong())

            every { productRepository.findAll(pageable = pageable, name = name) } returns page

            val result = productService.findProducts(pageable = pageable, name = name)

            result.content shouldHaveSize products.size
        }
    }
})
