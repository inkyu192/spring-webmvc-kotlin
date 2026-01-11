package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.CurationDetailResult
import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.application.dto.result.CurationSummaryResult
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.infrastructure.persistence.dto.CursorPage

data class CurationListResponse(
    val category: CurationCategory,
    val size: Long,
    val curations: List<CurationSummaryResponse>,
) {
    companion object {
        fun from(
            category: CurationCategory,
            resultList: List<CurationSummaryResult>,
        ) = CurationListResponse(
            category = category,
            size = resultList.size.toLong(),
            curations = resultList.map { CurationSummaryResponse.from(it) },
        )
    }
}

data class CurationSummaryResponse(
    val id: Long,
    val title: String,
) {
    companion object {
        fun from(result: CurationSummaryResult) = CurationSummaryResponse(
            id = checkNotNull(result.id),
            title = result.title,
        )
    }
}

data class CurationDetailResponse(
    val id: Long,
    val title: String,
    val products: List<CurationProductResponse>,
) {
    companion object {
        fun from(result: CurationDetailResult) = CurationDetailResponse(
            id = result.id,
            title = result.title,
            products = result.products.map { CurationProductResponse.from(result = it) },
        )
    }
}

data class CurationCursorPageResponse(
    val page: CursorPageResponse,
    val products: List<CurationProductResponse>,
) {
    companion object {
        fun from(page: CursorPage<CurationProductResult>) = CurationCursorPageResponse(
            page = CursorPageResponse.from(page),
            products = page.content.map { CurationProductResponse.from(result = it) },
        )
    }
}

data class CurationOffsetPageResponse(
    val page: OffsetPageResponse,
    val products: List<CurationProductResponse>,
) {
    companion object {
        fun from(page: org.springframework.data.domain.Page<CurationProductResult>) = CurationOffsetPageResponse(
            page = OffsetPageResponse.from(page),
            products = page.content.map { CurationProductResponse.from(result = it) },
        )
    }
}

data class CurationProductResponse(
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
) {
    companion object {
        fun from(result: CurationProductResult) = CurationProductResponse(
            category = result.category,
            name = result.name,
            description = result.description,
            price = result.price,
        )
    }
}