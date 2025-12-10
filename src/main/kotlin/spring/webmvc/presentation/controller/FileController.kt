package spring.webmvc.presentation.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import spring.webmvc.infrastructure.external.S3Service
import spring.webmvc.presentation.dto.response.FileResponse

@RestController
@RequestMapping("/files")
class FileController(
    private val s3Service: S3Service,
) {
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun uploadFile(
        @RequestPart file: MultipartFile,
    ): FileResponse {
        val key = s3Service.putObject(file)

        return FileResponse(key = key)
    }
}