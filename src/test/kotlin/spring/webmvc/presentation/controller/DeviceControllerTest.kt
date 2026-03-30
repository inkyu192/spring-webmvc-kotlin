package spring.webmvc.presentation.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.webmvc.application.service.DeviceService
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserDevice
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.infrastructure.config.ControllerTest
import spring.webmvc.infrastructure.security.SecurityContextUtil
import java.time.LocalDate

@ControllerTest([DeviceController::class])
class DeviceControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var deviceService: DeviceService

    private val userId = 1L
    private val deviceId = "550e8400-e29b-41d4-a716-446655440000"
    private val deviceName = "Chrome on MacOS"
    private lateinit var user: User
    private lateinit var userDevice: UserDevice

    @BeforeEach
    fun setUp() {
        mockkObject(SecurityContextUtil)
        every { SecurityContextUtil.getUserId() } returns userId

        user = User.create(
            name = "홍길동",
            phone = Phone.create("010-1234-5678"),
            gender = Gender.MALE,
            birthday = LocalDate.of(1990, 1, 1),
        )

        userDevice = UserDevice.create(
            user = user,
            deviceId = deviceId,
            deviceName = deviceName,
        )
    }

    @Test
    fun getMyDevices() {
        every { deviceService.getMyDevices(userId) } returns listOf(userDevice)

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/devices")
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                MockMvcRestDocumentation.document(
                    "device-list",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("size").description("디바이스 수"),
                        PayloadDocumentation.fieldWithPath("devices[].deviceId").description("디바이스 ID"),
                        PayloadDocumentation.fieldWithPath("devices[].deviceName").description("디바이스 이름"),
                        PayloadDocumentation.fieldWithPath("devices[].lastLoginAt").description("마지막 로그인 일시"),
                        PayloadDocumentation.fieldWithPath("devices[].createdAt").description("등록 일시"),
                    )
                )
            )
    }

    @Test
    fun deleteDevice() {
        every { deviceService.deleteDevice(userId, deviceId) } just runs

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/devices/{deviceId}", deviceId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                MockMvcRestDocumentation.document(
                    "device-delete",
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("액세스 토큰")
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("deviceId").description("디바이스 ID")
                    )
                )
            )
    }
}
