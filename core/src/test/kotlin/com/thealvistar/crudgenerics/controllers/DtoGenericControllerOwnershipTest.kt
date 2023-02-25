package com.thealvistar.crudgenerics.controllers

import com.ninjasquad.springmockk.MockkBean
import com.thealvistar.crudgenerics.entities.TestEntityWithOwnership
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
private class FakeBasicGenericOwnershipController :
    BasicGenericController<TestEntityWithOwnership, UUID>()

@WebMvcTest(FakeBasicGenericOwnershipController::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(FakeBasicGenericOwnershipController::class)
class DtoGenericControllerOwnershipTest(
    private val mockMvc: MockMvc,
    @MockkBean
    val service: GenericService<TestEntityWithOwnership, UUID>,
) {

    @Test
    fun updateOwnership() {
        every { service.updateOwnership(any(), any(), any()) } returns Unit

        val id = UUID.randomUUID()
        val newUUID = UUID.randomUUID()

        mockMvc.put("/test/$id/ownership") {
            contentType = MediaType.APPLICATION_JSON
            content = "{\"owner\": \"$newUUID\"}"
            principal = Principal { "john" }
        }
            .andExpect {
                status { isNoContent() }
            }

        verify {
            service.updateOwnership(
                id = id,
                newOwner = newUUID,
                principal = match { it.name == "john" },
            )
        }
    }
}
