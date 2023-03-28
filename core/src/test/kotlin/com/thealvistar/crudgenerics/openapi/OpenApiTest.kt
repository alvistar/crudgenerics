package com.thealvistar.crudgenerics.openapi

import com.thealvistar.crudgenerics.controllers.BasicGenericController
import com.thealvistar.crudgenerics.entities.TestEntity
import com.thealvistar.crudgenerics.entities.TestEntityWithOwnership
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/test")
@Profile("openApi")
private class FakeBasicGenericController : BasicGenericController<TestEntity, UUID>()

@RestController
@RequestMapping("/test2")
@Profile("openApi")
private class FakeBasicGenericController2 : BasicGenericController<TestEntityWithOwnership, UUID>()

@SpringBootTest()
@ActiveProfiles("openApi")
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class OpenApiTest(
    private val mockMvc: MockMvc,
) {
    @Test
    fun openApi() {
        mockMvc.get("/v3/api-docs")
            .andExpect {
                status { isOk() }

                val schema = "\$.paths['/test/{id}'].put.requestBody." +
                    "content['application/json'].schema"

                jsonPath("$schema.\$ref") { value("#/components/schemas/TestEntity") }
                jsonPath("\$.components['schemas']['TestEntity']") { exists() }

                val schema2 = "\$.paths['/test2/{id}'].put.requestBody." +
                    "content['application/json'].schema"

                jsonPath("$schema2.\$ref") { value("#/components/schemas/TestEntityWithOwnership") }
                jsonPath("\$.components['schemas']['TestEntityWithOwnership']") { exists() }
            }
    }
}

@SpringBootTest()
@TestPropertySource(properties = ["springdoc.use-fqn=true"])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("openApi")
@AutoConfigureMockMvc
class OpenApiFQN(
    private val mockMvc: MockMvc,
) {
    @Test
    fun openApi() {
        mockMvc.get("/v3/api-docs")
            .andExpect {
                status { isOk() }

                val schema = "\$.paths['/test/{id}'].put.requestBody." +
                    "content['application/json'].schema"

                jsonPath("$schema.\$ref") { value("#/components/schemas/com.thealvistar.crudgenerics.entities.TestEntity") }
                jsonPath("\$.components['schemas']['com.thealvistar.crudgenerics.entities.TestEntity']") { exists() }

                val schema2 = "\$.paths['/test2/{id}'].put.requestBody." +
                    "content['application/json'].schema"

                jsonPath("$schema2.\$ref") { value("#/components/schemas/com.thealvistar.crudgenerics.entities.TestEntityWithOwnership") }
                jsonPath("\$.components['schemas']['com.thealvistar.crudgenerics.entities.TestEntityWithOwnership']") { exists() }
            }
    }
}
