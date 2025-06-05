package spring.webmvc.domain.model.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "notification")
class Notification protected constructor(
    val memberId: Long,
    val title: String,
    val message: String,
    val url: String,
    isRead: Boolean,

    @Indexed(expireAfter = "1d")
    var createdAt: Instant = Instant.now()
) {
    @Id
    var id: String? = null
        protected set

    var isRead = isRead
        protected set

    companion object {
        fun create(memberId: Long, title: String, message: String, url: String) =
            Notification(
                memberId = memberId,
                title = title,
                message = message,
                url = url,
                isRead = false,
            )
    }
}