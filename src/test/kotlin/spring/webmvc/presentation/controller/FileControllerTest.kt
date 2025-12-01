package spring.webmvc.presentation.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.domain.model.enums.FileType
import spring.webmvc.infrastructure.config.WebMvcTestConfig
import spring.webmvc.infrastructure.external.S3Service
import spring.webmvc.presentation.controller.support.MockMvcRestDocsSetup
import java.nio.charset.StandardCharsets

@WebMvcTest(FileController::class)
@Import(WebMvcTestConfig::class)
class FileControllerTest : MockMvcRestDocsSetup() {
    @MockitoBean
    private lateinit var s3Service: S3Service
    private lateinit var file: MockMultipartFile
    private lateinit var data: MockMultipartFile
    private lateinit var key: String

    @BeforeEach
    fun setUp() {
        file = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test-image-content".toByteArray()
        )

        data = MockMultipartFile(
            "request",
            "",
            "application/json",
            "{\"type\": \"TEMP\"}".toByteArray(StandardCharsets.UTF_8)
        )

        key = "profile/20240610/uuid.jpg"
    }

    @Test
    fun uploadFile() {
        whenever(methodCall = s3Service.putObject(fileType = any(), file = any())).thenReturn(key)

        // When & Then
        mockMvc.perform(
            RestDocumentationRequestBuilders.multipart("/files")
                .file(file)
                .file(data)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "file-upload",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.requestParts(
                        RequestDocumentation.partWithName("file").description("파일"),
                        RequestDocumentation.partWithName("request").description("요청 데이터")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("key").description("키")
                    )
                )
            )
    }
}