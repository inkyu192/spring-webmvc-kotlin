package spring.webmvc.presentation.dto.response

data class FileResponse(
    val key: String,
    val url: String,
) {
    companion object {
        fun of(key: String, cloudfrontDomain: String) = FileResponse(
            key = key,
            url = "$cloudfrontDomain/$key",
        )
    }
}
