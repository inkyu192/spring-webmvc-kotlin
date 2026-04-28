package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.*
import spring.webmvc.domain.model.enums.CurationPlacement
import spring.webmvc.domain.model.enums.CurationType
import spring.webmvc.domain.model.vo.CurationAttribute

data class CurationListResponse(
    val size: Long,
    val curations: List<CurationSummaryResponse>,
) {
    companion object {
        fun of(results: List<CurationSummaryResult>) = CurationListResponse(
            size = results.size.toLong(),
            curations = results.map { CurationSummaryResponse.of(it) },
        )
    }
}

data class CurationSummaryResponse(
    val id: Long,
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType,
    val exposureAttribute: CurationExposureAttributeResponse,
) {
    companion object {
        fun of(result: CurationSummaryResult) = CurationSummaryResponse(
            id = checkNotNull(result.id),
            title = result.title,
            placement = result.placement,
            type = result.type,
            exposureAttribute = CurationExposureAttributeResponse.of(result.exposureAttribute),
        )
    }
}

data class CurationDetailResponse(
    val id: Long,
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType,
    val attribute: CurationAttributeResponse,
    val exposureAttribute: CurationExposureAttributeResponse,
    val products: List<CurationProductResponse>,
) {
    companion object {
        fun of(result: CurationDetailResult) = CurationDetailResponse(
            id = result.id,
            title = result.title,
            placement = result.placement,
            type = result.type,
            attribute = CurationAttributeResponse.of(result.attribute),
            exposureAttribute = CurationExposureAttributeResponse.of(result.exposureAttribute),
            products = result.products.map { CurationProductResponse.of(result = it) },
        )
    }
}

data class CurationDetailCursorPageResponse(
    val id: Long,
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType,
    val exposureAttribute: CurationExposureAttributeResponse,
    val products: CursorPageResponse<CurationProductResponse>,
) {
    companion object {
        fun of(result: CurationCursorPageResult) = CurationDetailCursorPageResponse(
            id = result.id,
            title = result.title,
            placement = result.placement,
            type = result.type,
            exposureAttribute = CurationExposureAttributeResponse.of(result.exposureAttribute),
            products = CursorPageResponse.of(result.productPage) { CurationProductResponse.of(result = it) },
        )
    }
}

data class CurationDetailOffsetPageResponse(
    val id: Long,
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType,
    val attribute: CurationAttributeResponse,
    val exposureAttribute: CurationExposureAttributeResponse,
    val products: OffsetPageResponse<CurationProductResponse>,
) {
    companion object {
        fun of(result: CurationOffsetPageResult) = CurationDetailOffsetPageResponse(
            id = result.id,
            title = result.title,
            placement = result.placement,
            type = result.type,
            attribute = CurationAttributeResponse.of(result.attribute),
            exposureAttribute = CurationExposureAttributeResponse.of(result.exposureAttribute),
            products = OffsetPageResponse.of(result.productPage) { CurationProductResponse.of(result = it) },
        )
    }
}

data class CurationProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
    val exposureAttribute: ProductExposureAttributeResponse,
) {
    companion object {
        fun of(result: CurationProductResult) = CurationProductResponse(
            id = result.id,
            name = result.name,
            description = result.description,
            price = result.price,
            exposureAttribute = ProductExposureAttributeResponse.of(result.exposureAttribute),
        )
    }
}

data class CurationAttributeResponse(
    val keyword: String?,
) {
    companion object {
        fun of(attribute: CurationAttribute) = CurationAttributeResponse(
            keyword = attribute.keyword,
        )
    }
}
