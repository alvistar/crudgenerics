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
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.UUID

@Profile("test")
@RestController
@RequestMapping("/test")
class FakeController : VerySimpleGenericController<TestEntity, UUID>()

@WebMvcTest(FakeController::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(FakeController::class)
class GenericControllerTest(
    val mockMvc: MockMvc,
    @MockkBean val service: GenericService<TestEntity, UUID>
) {
    @Test
    fun `list resources`() {
        every { service.listResources(any(), any(), any()) } returns Page.empty()

        mockMvc.get("/test") {
            param("page", "0")
            param("size", "10")
            param("sort", "id,asc")
            param("filter", "name==test")
            principal = Principal { "john" }
        }
            .andExpect {
                status { isOk() }
            }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.listResources(
                filter = "name==test",
                pageable = match { it.pageSize == 10 && it.pageNumber == 0 },
                principal = match { it.name == "john" }
            )
        }
    }

    @Test
    fun `delete resource`() {
        val uuid = UUID.randomUUID()

        every { service.deleteResourceById(uuid, any()) } returns Unit

        mockMvc.delete("/test/$uuid") {
            principal = Principal { "john" }
        }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.deleteResourceById(uuid, match { it.name == "john" })
        }
    }

    @Test
    fun `delete resources by ids`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        every { service.deleteResourcesByIds(listOf(uuid1, uuid2), any()) } returns Unit

        mockMvc.delete("/test") {
            param("id", uuid1.toString())
            param("id", uuid2.toString())
            principal = Principal { "john" }
        }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.deleteResourcesByIds(listOf(uuid1, uuid2), match { it.name == "john" })
        }
    }

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
                dto = match { (it as TestEntity).name == "test" },
                principal = match { it.name == "john" }
            )
        }
    }

    @Test
    fun `get resources by ids`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        every { service.getResourcesByIds(listOf(uuid1, uuid2), any()) } returns listOf()

        mockMvc.get("/test") {
            param("id", uuid1.toString())
            param("id", uuid2.toString())
            principal = Principal { "john" }
        }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.getResourcesByIds(listOf(uuid1, uuid2), match { it.name == "john" })
        }
    }

    @Test
    fun `get resource by id`() {
        val uuid = UUID.randomUUID()

        every { service.getResourceById(uuid, any()) } returns TestEntity(
            id = uuid,
            name = "test"
        )

        mockMvc.get("/test/$uuid") {
            principal = Principal { "john" }
        }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.getResourceById(uuid, match { it.name == "john" })
        }
    }

    @Test
    fun `create resource`() {
        val resource = TestEntity(id = UUID.randomUUID(), name = "test")

        every { service.createResource(any()) } returns resource

        mockMvc.post("/test") {
            content = "{\"name\": \"test\"}"
            contentType = MediaType.APPLICATION_JSON
            principal = Principal { "john" }
        }.andExpect {
            status { isCreated() }
        }

        verify {
            service.createResource(
                dto = match { (it as TestEntity).name == "test" }
            )
        }
    }

    @Test
    fun `update ownership`() {
        val uuid = UUID.randomUUID()
        val newUUID = UUID.randomUUID()

        every { service.updateOwnership(uuid, any(), any()) } returns Unit

        mockMvc.put("/test/$uuid/ownership") {
            content = "{\"name\": \"test\", \"owner\": \"$newUUID\"}"
            contentType = MediaType.APPLICATION_JSON
            principal = Principal { uuid.toString() }
        }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.updateOwnership(
                id = uuid,
                newOwner = newUUID,
                principal = any()
            )
        }
    }
}
