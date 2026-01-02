package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.application.dto.result.CurationResult

data class CurationListResponse(
    val count: Long,
    val curations: List<CurationResponse>,
) {
    companion object {
        fun from(resultList: List<CurationResult>): CurationListResponse {
            return CurationListResponse(
                count = resultList.size.toLong(),
                curations = resultList.map { CurationResponse.from(it) }
            )
        }
    }
}

data class CurationResponse(
    val id: Long,
    val title: String,
    val category: String,
) {
    companion object {
        fun from(result: CurationResult): CurationResponse {
            return CurationResponse(
                id = checkNotNull(result.id),
                title = result.title,
                category = result.category.name,
            )
        }
    }
}

data class CurationProductResponse(
    val id: Long,
    val title: String,
    val page: CursorPageResponse,
    val products: List<ProductResponse>,
) {
    companion object {
        fun from(result: CurationProductResult): CurationProductResponse {
            return CurationProductResponse(
                id = checkNotNull(result.curation.id),
                title = result.curation.title,
                page = CursorPageResponse.from(result.productPage),
                products = result.productPage.content.map { productResult ->
                    ProductResponse.from(productResult)
                },
            )
        }
    }
}