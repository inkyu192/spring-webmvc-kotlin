package spring.webmvc.presentation.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.infrastructure.config.ControllerTest
import spring.webmvc.infrastructure.external.s3.S3Service
import spring.webmvc.infrastructure.properties.AwsProperties

@ControllerTest([FileController::class])
class FileControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var s3Service: S3Service

    @MockkBean
    private lateinit var awsProperties: AwsProperties
    private lateinit var file: MockMultipartFile
    private lateinit var key: String

    @BeforeEach
    fun setUp() {
        file = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test-image-content".toByteArray()
        )

        key = "temp/uuid.jpg"
    }

    @Test
    fun uploadFile() {
        every { s3Service.putObject(file = any()) } returns key
        every { awsProperties.cloudfront.domain } returns "http://localhost:4566/my-bucket"

        // When & Then
        mockMvc.perform(
            RestDocumentationRequestBuilders.multipart("/files")
                .file(file)
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
                        RequestDocumentation.partWithName("file").description("파일")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("key").description("키"),
                        PayloadDocumentation.fieldWithPath("url").description("URL")
                    )
                )
            )
    }
}
