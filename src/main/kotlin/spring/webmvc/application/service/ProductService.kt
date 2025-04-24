package spring.webmvc.application.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.presentation.dto.response.ProductResponse

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun findProducts(pageable: Pageable, name: String?) =
        productRepository.findAll(pageable = pageable, name = name).map { ProductResponse(product = it) }
}