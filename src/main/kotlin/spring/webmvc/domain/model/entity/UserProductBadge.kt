package spring.webmvc.domain.model.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class UserProductBadge(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    var pk: String,

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    var sk: String,

    @get:DynamoDbAttribute("isRecommended")
    var isRecommended: Boolean,

    @get:DynamoDbAttribute("isPersonalPick")
    var isPersonalPick: Boolean,

    @get:DynamoDbAttribute("createdAt")
    var createdAt: String,

    @get:DynamoDbAttribute("updatedAt")
    var updatedAt: String,
) {
    companion object {
        const val TABLE_NAME = "user_product_badge"
    }
}
