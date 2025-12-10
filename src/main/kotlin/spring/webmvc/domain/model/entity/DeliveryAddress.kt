package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.vo.Phone

@Entity
class DeliveryAddress protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
    name: String,
    recipientName: String,
    recipientPhone: Phone,
    postalCode: String,
    address: String,
    addressDetail: String,
    isDefault: Boolean,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var name = name
        protected set

    var recipientName = recipientName
        protected set

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "recipient_phone"))
    var recipientPhone = recipientPhone
        protected set

    var postalCode = postalCode
        protected set

    var address = address
        protected set

    var addressDetail = addressDetail
        protected set

    var isDefault = isDefault
        protected set
}
