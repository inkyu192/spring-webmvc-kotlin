package spring.webmvc.infrastructure.common

import org.springframework.web.multipart.MultipartFile

object FileUtil {
    fun validate(fileType: FileType, file: MultipartFile) {
        val filename = file.originalFilename
            ?: throw IllegalArgumentException("파일 이름이 존재하지 않습니다.")

        if (!filename.contains(".")) {
            throw IllegalArgumentException("확장자가 없는 파일입니다.")
        }

        val extension = extractExtension(filename)
        if (!fileType.allowedExtensions.contains(extension)) {
            throw IllegalArgumentException("허용되지 않은 확장자입니다: $extension")
        }

        if (file.size > fileType.maxSize) {
            throw IllegalArgumentException("파일 크기가 허용된 범위를 초과했습니다.")
        }
    }

    fun extractExtension(filename: String): String {
        val lastDot = filename.lastIndexOf('.')
        return if (lastDot == -1 || lastDot == filename.length - 1) {
            "bin"
        } else {
            filename.substring(lastDot + 1)
        }
    }
}