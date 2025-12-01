package spring.webmvc.presentation.dto.request

import spring.webmvc.domain.model.enums.FileType

data class FileUploadRequest(
    val type: FileType,
)
