package spring.webmvc.application.dto.result

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.UserProductBadge
import spring.webmvc.domain.model.enums.CurationPlacement
import spring.webmvc.domain.model.enums.CurationType
import spring.webmvc.domain.model.vo.CurationAttribute

data class CurationSummaryResult(
    val id: Long,
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType,
    val exposureAttribute: CurationExposureAttributeResult,
) {
    companion object {
        fun of(curation: Curation) = CurationSummaryResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            placement = curation.placement,
            type = curation.type,
            exposureAttribute = CurationExposureAttributeResult.of(curation.exposureAttribute),
        )
    }
}

data class CurationDetailResult(
    val id: Long,
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType,
    val attribute: CurationAttribute,
    val exposureAttribute: CurationExposureAttributeResult,
    val products: List<CurationProductResult>,
) {
    companion object {
        fun of(curation: Curation) = CurationDetailResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            placement = curation.placement,
            type = curation.type,
            attribute = curation.attribute,
            exposureAttribute = CurationExposureAttributeResult.of(curation.exposureAttribute),
            products = curation.curationProducts.map { CurationProductResult.of(curationProduct = it) },
        )
    }
}

data class CurationCursorPageResult(
    val id: Long,
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType,
    val exposureAttribute: CurationExposureAttributeResult,
    val productPage: CursorPage<CurationProductResult>,
) {
    companion object {
        fun of(
            curation: Curation,
            page: CursorPage<CurationProduct>,
            badgeMap: Map<Long, UserProductBadge> = emptyMap(),
        ) = CurationCursorPageResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            placement = curation.placement,
            type = curation.type,
            exposureAttribute = CurationExposureAttributeResult.of(curation.exposureAttribute),
            productPage = page.map { CurationProductResult.of(curationProduct = it, badge = badgeMap[it.product.id]) },
        )

        fun of(
            curation: Curation,
            products: List<Product>,
            badgeMap: Map<Long, UserProductBadge> = emptyMap(),
        ) = CurationCursorPageResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            placement = curation.placement,
            type = curation.type,
            exposureAttribute = CurationExposureAttributeResult.of(curation.exposureAttribute),
            productPage = CursorPage(
                content = products.map { CurationProductResult.of(product = it, badge = badgeMap[it.id]) },
                size = products.size.toLong(),
                hasNext = false,
                nextCursorId = null,
            ),
        )
    }
}

data class CurationOffsetPageResult(
    val id: Long,
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType,
    val attribute: CurationAttribute,
    val exposureAttribute: CurationExposureAttributeResult,
    val productPage: Page<CurationProductResult>,
) {
    companion object {
        fun of(
            curation: Curation,
            page: Page<CurationProduct>,
        ) = CurationOffsetPageResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            placement = curation.placement,
            type = curation.type,
            attribute = curation.attribute,
            exposureAttribute = CurationExposureAttributeResult.of(curation.exposureAttribute),
            productPage = page.map { CurationProductResult.of(curationProduct = it) },
        )

        fun ofProducts(
            curation: Curation,
            page: Page<Product>,
        ) = CurationOffsetPageResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            placement = curation.placement,
            type = curation.type,
            attribute = curation.attribute,
            exposureAttribute = CurationExposureAttributeResult.of(curation.exposureAttribute),
            productPage = page.map { CurationProductResult.of(product = it) },
        )

        fun empty(curation: Curation) = CurationOffsetPageResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            placement = curation.placement,
            type = curation.type,
            attribute = curation.attribute,
            exposureAttribute = CurationExposureAttributeResult.of(curation.exposureAttribute),
            productPage = PageImpl(emptyList()),
        )
    }
}

data class CurationProductResult(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
    val exposureAttribute: ProductExposureAttributeResult,
) {
    companion object {
        fun of(curationProduct: CurationProduct, badge: UserProductBadge? = null) = CurationProductResult(
            id = checkNotNull(curationProduct.product.id),
            name = curationProduct.product.name,
            description = curationProduct.product.description,
            price = curationProduct.product.price,
            exposureAttribute = ProductExposureAttributeResult.of(curationProduct.product.exposureAttribute, badge),
        )

        fun of(product: Product, badge: UserProductBadge? = null) = CurationProductResult(
            id = checkNotNull(product.id),
            name = product.name,
            description = product.description,
            price = product.price,
            exposureAttribute = ProductExposureAttributeResult.of(product.exposureAttribute, badge),
        )
    }
}
