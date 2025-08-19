package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.application.dto.result.CurationResult

data class CurationCreateResponse(
    val id: Long,
)

data class CurationListResponse(
    val count: Long,
    val content: List<CurationResponse>,
) {
    constructor(resultList: List<CurationResult>) : this(
        count = resultList.size.toLong(),
        content = resultList.map { CurationResponse(result = it) }
    )
}

data class CurationResponse(
    val id: Long,
    val title: String,
) {
    constructor(result: CurationResult) : this(
        id = checkNotNull(result.id),
        title = result.title,
    )
}

data class CurationProductResponse(
    val id: Long,
    val title: String,
    val page: CursorPageResponse,
    val products: List<ProductResponse>,
) {
    constructor(result: CurationProductResult) : this(
        id = checkNotNull(result.curation.id),
        title = result.curation.title,
        page = CursorPageResponse(result.productPage),
        products = result.productPage.content.map { ProductResponse(productResult = it) },
    )
}