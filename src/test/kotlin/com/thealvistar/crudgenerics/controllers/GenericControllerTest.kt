package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.services.GenericService
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.core.ResolvableType
import org.springframework.test.context.TestConstructor
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Profile("test")
@Entity
class MyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var name: String? = null
)

@TestConfiguration
class MyEntityService : GenericService<MyEntity, UUID>()

@Profile("test")
@RestController
@RequestMapping("/test")
class MyTestController : VerySimpleGenericController<MyEntity, UUID>()

@WebMvcTest(MyTestController::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(MyTestController::class)
class GenericControllerTest(val applicationContext: ApplicationContext, val controller: MyTestController) {
    @Test
    fun `service should exist`() {
        controller.service shouldNotBe null
    }
}
