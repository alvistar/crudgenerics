package com.thealvistar.crudgenerics.controllers

import com.ninjasquad.springmockk.MockkBean
import com.thealvistar.crudgenerics.entities.TestEntity
import com.thealvistar.crudgenerics.services.GenericService
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.put
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.UUID

@Profile("test")
@RestController
@RequestMapping("/test")
private class FakeBasicGenericController : BasicGenericController<TestEntity, UUID>()

@WebMvcTest(FakeBasicGenericController::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(FakeBasicGenericController::class)
class BasicGenericControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean val service: GenericService<TestEntity, UUID>
) {

    @Test
    fun `update resource by id`() {
        val uuid = UUID.randomUUID()
        val resource = TestEntity(id = uuid, name = "test")

        every { service.updateResourceById(uuid, any(), any()) } returns resource

        mockMvc.put("/test/$uuid") {
            content = "{\"name\": \"test\"}"
            contentType = MediaType.APPLICATION_JSON
            principal = Principal { "john" }
        }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.updateResourceById(
                id = uuid,
                dto = "{\"name\": \"test\"}",
                principal = match { it.name == "john" }
            )
        }
    }
}
