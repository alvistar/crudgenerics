package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.TestEntity
import com.thealvistar.crudgenerics.entities.TestEntityWithOwnership
import com.thealvistar.crudgenerics.repositories.TestRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.test.context.TestConstructor
import java.security.Principal
import java.util.UUID

@TestConfiguration
class TestService : GenericService<TestEntity, UUID>()

@TestConfiguration
class TestOwnerService : GenericService<TestEntityWithOwnership, UUID>()

interface MyView {
    val name: String
}

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(TestService::class, TestOwnerService::class)
@AutoConfigureJson
class GenericServiceTest(
    val repository: TestRepository,
    val service: TestService,
    val testOwnerService: TestOwnerService
) {

    @Test
    fun `first test`() {
        repository.count() shouldBe 0
    }

    @Test
    fun `get entity class`() {
        service.resolveEntityClass() shouldBe TestEntity::class.java
    }

    @Test
    fun `check ownership`() {
        service.hasOwnership() shouldBe false
    }

    @Test
    fun `check ownership true`() {
        testOwnerService.hasOwnership() shouldBe false
    }

    @Test
    fun `list resources`() {
        val principal = mockk<Principal>()
        every { principal.name } returns UUID.randomUUID().toString()

        service.listResources(pageable = Pageable.unpaged(), principal = principal)
            .count() shouldBe 0
    }

    @Test
    fun `list resources with projection`() {
        val principal = mockk<Principal>()
        every { principal.name } returns UUID.randomUUID().toString()

        val entities = (1..3).map {
            val e = TestEntity(name = "test$it")
            repository.save(e)
        }

        service.listResources(
            pageable = Pageable.unpaged(),
            projection = MyView::class.java
        ).apply {
            size shouldBe 3
            get().toList()[0].shouldBeInstanceOf<MyView>()
        }
    }

    @Test
    fun `update resource`() {

        val entity = TestEntity(name = "test")
        repository.save(entity)

        val newEntity = TestEntity(id = entity.id, name = "test2")
        service.updateResourceById(entity.id!!, newEntity)

        repository.findAll().apply {
            size shouldBe 1
            get(0).name shouldBe "test2"
        }
    }
}
