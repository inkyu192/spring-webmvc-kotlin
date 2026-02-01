package spring.webmvc.domain.model.enums

enum class OauthProvider(
    val description: String,
) {
    GOOGLE("구글"),
    KAKAO("카카오"),
    APPLE("애플"),
    ;
}
