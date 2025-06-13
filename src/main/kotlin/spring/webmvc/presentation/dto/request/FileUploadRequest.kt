package spring.webmvc.presentation.dto.request

import spring.webmvc.infrastructure.common.FileType

data class FileUploadRequest(
    val type: FileType,
)
