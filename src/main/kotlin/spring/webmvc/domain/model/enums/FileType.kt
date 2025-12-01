package spring.webmvc.domain.model.enums

enum class FileType(
    val directory: String,
    val allowedExtensions: List<String>,
    val maxSize: Long,
) {
    PROFILE(directory = "profile", allowedExtensions = listOf("png", "jpg"), maxSize = 10 * 1024 * 1024),
    BANNER(directory = "banner", allowedExtensions = listOf("png", "jpg"), maxSize = 10 * 1024 * 1024),
    TEMP(directory = "temp", allowedExtensions = listOf("jpg", "png", "pdf"), maxSize = 10 * 1024 * 1024),
    ;
}