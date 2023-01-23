package com.thealvistar.crudgenerics.controllers

import com.ninjasquad.springmockk.MockkBean
import com.thealvistar.crudgenerics.entities.TestEntity
import com.thealvistar.crudgenerics.services.GenericService
import io.mockk.every
import io.mockk.mockk
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

private class MyDto

private interface MyProjection

@Profile("test")
@RestController
@RequestMapping("/test")
private class FakeGenericController : GenericController<TestEntity, UUID, MyDto, MyProjection>()

@WebMvcTest(FakeGenericController::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(FakeGenericController::class)
class GenericControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean val service: GenericService<TestEntity, UUID>
) {

    @Test
    fun listResources() {
        every {
            service.listResources<MyProjection>(any(), any(), any(), any())
        } returns Page.empty()

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
                principal = match { it.name == "john" },
                projection = MyProjection::class
            )
        }
    }

    @Test
    fun createResource() {
        every { service.createResource(any(), MyProjection::class) } returns mockk()

        mockMvc.post("/test") {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }
            .andExpect {
                status { isCreated() }
            }

        verify {
            service.createResource(
                dto = match { it is MyDto },
                clazz = MyProjection::class
            )
        }
    }

    @Test
    fun getResourceById() {
        every { service.getResourceById(any(), any(), MyProjection::class) } returns mockk()

        val id = UUID.randomUUID()

        mockMvc.get("/test/$id") {
            principal = Principal { "john" }
        }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.getResourceById(
                id = id,
                principal = match { it.name == "john" },
                clazz = MyProjection::class
            )
        }
    }

    @Test
    fun getResourcesByIds() {
        every { service.getResourcesByIds(any(), any(), MyProjection::class) } returns listOf()

        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        mockMvc.get("/test") {
            param("id", uuid1.toString())
            param("id", uuid2.toString())
            principal = Principal { "john" }
        }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.getResourcesByIds(
                ids = listOf(uuid1, uuid2),
                principal = match { it.name == "john" },
                clazz = MyProjection::class
            )
        }
    }

    @Test
    fun updateResourceById() {
        every {
            service.updateResourceById(
                any(),
                any(),
                any(),
                MyProjection::class
            )
        } returns mockk()

        val id = UUID.randomUUID()

        mockMvc.put("/test/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
            principal = Principal { "john" }
        }
            .andExpect {
                status { isOk() }
            }

        verify {
            service.updateResourceById(
                id = id,
                dto = match { it is MyDto },
                principal = match { it.name == "john" },
                clazz = MyProjection::class
            )
        }
    }

    @Test
    fun deleteResourceById() {
        every { service.deleteResourceById(any(), any()) } returns Unit

        val id = UUID.randomUUID()

        mockMvc.delete("/test/$id") {
            principal = Principal { "john" }
        }
            .andExpect {
                status { isNoContent() }
            }

        verify {
            service.deleteResourceById(
                id = id,
                principal = match { it.name == "john" }
            )
        }
    }

    @Test
    fun deleteResourcesByIds() {
        every { service.deleteResourcesByIds(any(), any()) } returns Unit

        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        mockMvc.delete("/test") {
            param("id", uuid1.toString())
            param("id", uuid2.toString())
            principal = Principal { "john" }
        }
            .andExpect {
                status { isNoContent() }
            }

        verify {
            service.deleteResourcesByIds(
                ids = listOf(uuid1, uuid2),
                principal = match { it.name == "john" }
            )
        }
    }

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
                principal = match { it.name == "john" }
            )
        }
    }
}
