package spring.webmvc.domain.model.enums

enum class MemberStatus(
    val description: String
) {
    PENDING("대기"),
    ACTIVE("활성"),
    SUSPENDED("정지"),
    WITHDRAWN("탈퇴"),
}