package spring.webmvc.domain.model.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class UserCurationProduct(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    var pk: String,

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    var sk: String,

    @get:DynamoDbAttribute("productIds")
    var productIds: List<Long>,

    @get:DynamoDbAttribute("createdAt")
    var createdAt: String,

    @get:DynamoDbAttribute("updatedAt")
    var updatedAt: String,
) {
    companion object {
        const val TABLE_NAME = "user_curation_product"
    }
}
