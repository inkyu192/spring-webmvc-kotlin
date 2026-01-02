package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.vo.BusinessNumber
import spring.webmvc.domain.model.vo.Phone

@Entity
class Company protected constructor(
    name: String,
    businessNumber: BusinessNumber,
    address: String,
    phone: Phone,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var name = name
        protected set

    @Embedded
    var businessNumber = businessNumber
        protected set

    var address = address
        protected set

    @Embedded
    var phone = phone
        protected set
}