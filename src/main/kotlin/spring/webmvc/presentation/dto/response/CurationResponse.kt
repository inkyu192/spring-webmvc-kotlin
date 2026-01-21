package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.*
import spring.webmvc.domain.model.enums.CurationCategory

data class CurationListResponse(
    val size: Long,
    val curations: List<CurationSummaryResponse>,
) {
    companion object {
        fun of(resultList: List<CurationSummaryResult>) = CurationListResponse(
            size = resultList.size.toLong(),
            curations = resultList.map { CurationSummaryResponse.of(it) },
        )
    }
}

data class CurationSummaryResponse(
    val id: Long,
    val title: String,
    val category: CurationCategory,
) {
    companion object {
        fun of(result: CurationSummaryResult) = CurationSummaryResponse(
            id = checkNotNull(result.id),
            title = result.title,
            category = result.category,
        )
    }
}

data class CurationDetailResponse(
    val id: Long,
    val title: String,
    val category: CurationCategory,
    val products: List<CurationProductResponse>,
) {
    companion object {
        fun of(result: CurationDetailResult) = CurationDetailResponse(
            id = result.id,
            title = result.title,
            category = result.category,
            products = result.products.map { CurationProductResponse.of(result = it) },
        )
    }
}

data class CurationDetailCursorPageResponse(
    val id: Long,
    val title: String,
    val category: CurationCategory,
    val products: CursorPageResponse<CurationProductResponse>,
) {
    companion object {
        fun of(result: CurationCursorPageResult) = CurationDetailCursorPageResponse(
            id = result.id,
            title = result.title,
            category = result.category,
            products = CursorPageResponse.of(result.productPage) { CurationProductResponse.of(result = it) },
        )
    }
}

data class CurationDetailOffsetPageResponse(
    val id: Long,
    val title: String,
    val category: CurationCategory,
    val products: OffsetPageResponse<CurationProductResponse>,
) {
    companion object {
        fun of(result: CurationOffsetPageResult) = CurationDetailOffsetPageResponse(
            id = result.id,
            title = result.title,
            category = result.category,
            products = OffsetPageResponse.of(result.productPage) { CurationProductResponse.of(result = it) },
        )
    }
}

data class CurationProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
) {
    companion object {
        fun of(result: CurationProductResult) = CurationProductResponse(
            id = result.id,
            name = result.name,
            description = result.description,
            price = result.price,
        )
    }
}