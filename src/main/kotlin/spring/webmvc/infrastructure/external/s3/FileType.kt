package spring.webmvc.infrastructure.external.s3

enum class FileType(
    val path: String,
) {
    PROFILE(path = "profile"),
    BANNER(path = "banner"),
    ;
}