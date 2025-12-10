package spring.webmvc.infrastructure.external

enum class FileType(
    val path: String,
) {
    PROFILE(path = "profile"),
    BANNER(path = "banner"),
    ;
}