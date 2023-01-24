package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.TestEntity
import com.thealvistar.crudgenerics.entities.TestEntityWithOwnership
import com.thealvistar.crudgenerics.exceptions.ForbiddenException
import com.thealvistar.crudgenerics.repositories.TestRepository
import com.thealvistar.crudgenerics.repositories.TestWithOwnershipRepository
import io.github.perplexhub.rsql.RSQLJPAAutoConfiguration
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.test.context.TestConstructor
import java.security.Principal
import java.util.UUID

@TestConfiguration
class TestService : GenericService<TestEntity, UUID>()

val mockSecurityFilter = mockk<SecurityFilter<TestEntity>>()

@TestConfiguration
class TestSecurityService : GenericService<TestEntity, UUID>(mockSecurityFilter)

@TestConfiguration
class TestOwnershipService : GenericService<TestEntityWithOwnership, UUID>()

interface MyView {
    val name: String
    val reference: TestEntityWithOwnership
}

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DataJpaTest
@Import(TestService::class, TestSecurityService::class, TestOwnershipService::class)
@AutoConfigureJson
@ImportAutoConfiguration(ValidationAutoConfiguration::class, RSQLJPAAutoConfiguration::class)
@ExtendWith(MockKExtension::class)
class GenericServiceTest(
    val repository: TestRepository,
    val service: TestService,
    val securityService: TestSecurityService,
    val ownershipService: TestOwnershipService,
    val ownerRepository: TestWithOwnershipRepository
) {
    val principal = mockk<Principal>()

    init {
        every { principal.name } returns "TestPrincipal"
    }

    @BeforeEach
    fun beforeEach() {
    }

    @Test
    fun `first test`() {
        repository.count() shouldBe 0
    }

    @Test
    fun `get entity class`() {
        service.resolveEntityClass() shouldBe TestEntity::class.java
    }

    @Test
    fun `list resources`() {
        val entity = repository.save(TestEntity(name = "TestEntity"))

        service.listResources(pageable = Pageable.unpaged()).apply {
            count() shouldBe 1
            first().id shouldBe entity.id
        }
    }

    @Test
    fun `list resources with security`() {
        val entities = (1..3).map { repository.save(TestEntity(name = "TestEntity$it")) }

        every { mockSecurityFilter.getSpecificationForList(principal) }.returns(
            Specification.where { root, _, cb ->
                cb.notEqual(root.get<String>("name"), "TestEntity1")
            }
        )

        securityService.listResources(principal = principal).apply {
            count() shouldBe 2
            this.shouldNotContain(entities[0])
        }
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
            projection = MyView::class
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

    @Test
    fun `update resource from json`() {
        val entityA = TestEntityWithOwnership(name = "testA", owner = UUID.randomUUID())
        ownerRepository.save(entityA)

        val entityB = TestEntity(name = "testB", reference = entityA)
        repository.save(entityB)

        @Language("JSON")
        val json = """
            {
                "name": "testC"
            }
        """.trimIndent()

        service.updateResourceById(entityB.id!!, json)

        repository.findAll().apply {
            size shouldBe 1
            get(0).name shouldBe "testC"
            get(0).reference?.name shouldBe "testA"
        }
    }

    @Test
    fun `update resource with projection`() {
        val entity = TestEntity(name = "test")
        repository.save(entity)

        val newEntity = TestEntity(id = entity.id, name = "test2")
        service.updateResourceById(entity.id!!, newEntity, clazz = MyView::class)

        repository.findAll().apply {
            size shouldBe 1
            get(0).name shouldBe "test2"
        }
    }

    @Test
    fun `update resource with security filter`() {
        val entity = TestEntity(name = "test")
        repository.save(entity)

        val newEntity = TestEntity(id = entity.id, name = "test2")
        every { mockSecurityFilter.canView(entity, principal) }.returns(Unit)
        every { mockSecurityFilter.canUpdate(newEntity, any()) }.returns(Unit)

        securityService.updateResourceById(entity.id!!, dto = newEntity, principal = principal)

        verify(exactly = 1) { mockSecurityFilter.canView(entity, principal) }
        verify(exactly = 1) { mockSecurityFilter.canUpdate(newEntity, principal) }
    }

    @Test
    fun `update resource with security filter - unauthorized`() {
        val entity = TestEntity(name = "test")
        repository.save(entity)

        val newEntity = TestEntity(id = entity.id, name = "test2")
        every {
            mockSecurityFilter.canView(
                entity,
                principal
            )
        }.throws(ForbiddenException())

        shouldThrow<ForbiddenException> {
            securityService.updateResourceById(
                entity.id!!,
                dto = newEntity,
                principal = principal
            )
        }
    }

    @Test
    fun `create resource with projection`() {
        val entity = TestEntity(name = "test")
        service.createResource(entity, clazz = MyView::class).shouldBeInstanceOf<MyView>()

        repository.findAll().apply {
            size shouldBe 1
            get(0).name shouldBe "test"
        }
    }

    @Test
    fun `create resource`() {
        val entity = TestEntity(name = "test")
        service.createResource(entity).shouldBeInstanceOf<TestEntity>()

        repository.findAll().apply {
            size shouldBe 1
            get(0).name shouldBe "test"
        }
    }

    @Test
    fun `create resource with security filter`() {
        val entity = TestEntity(name = "test")
        every { mockSecurityFilter.canCreate(entity, principal) }.returns(Unit)

        securityService.createResource(entity, principal = principal)

        verify(exactly = 1) { mockSecurityFilter.canCreate(entity, principal) }
    }

    @Test
    fun `create resource with projection and relation`() {
        every { mockSecurityFilter.canCreate(any(), any()) }.returns(Unit)

        val entityWithOwnership = TestEntityWithOwnership().apply {
            name = "ownerEntity"
            ownerRepository.save(this)
        }

        val refEntity = TestEntityWithOwnership().apply {
            id = entityWithOwnership.id
        }

        val entity = TestEntity(name = "test", reference = refEntity)

        securityService.createResource(entity, null, MyView::class).apply {
            reference.name shouldBe "ownerEntity"
        }
    }

    @Test
    fun save() {
        service.save(TestEntity(name = "test"))

        repository.findAll().apply {
            size shouldBe 1
            get(0).name shouldBe "test"
        }
    }

    @Test
    fun `get resource by id`() {
        val entity = TestEntity(name = "test")
        repository.save(entity)

        service.getResourceById(entity.id!!).shouldBeInstanceOf<TestEntity>()
    }

    @Test
    fun `get resource by id and projection`() {
        val entity = TestEntity(name = "test")
        repository.save(entity)

        service.getResourceById(entity.id!!, clazz = MyView::class)
            .shouldBeInstanceOf<MyView>()
    }

    @Test
    fun `get resource by id and projection - security`() {
        val entity = TestEntity(name = "test")
        repository.save(entity)

        every { mockSecurityFilter.canView(entity, principal) }.returns(Unit)

        securityService.getResourceById(
            entity.id!!,
            clazz = MyView::class,
            principal = principal
        )
            .shouldBeInstanceOf<MyView>()

        verify(exactly = 1) { mockSecurityFilter.canView(entity, principal) }
    }

    @Test
    fun `get resources by ids and security`() {
        val entities = (1..3).map { repository.save(TestEntity(name = "TestEntity$it")) }

        every { mockSecurityFilter.canView(any(), principal) }.returns(Unit)

        securityService.getResourcesByIds(
            entities.map { it.id!! },
            principal = principal
        ).apply {
            count() shouldBe 3
        }

        verify(exactly = 3) { mockSecurityFilter.canView(any(), principal) }
    }

    @Test
    fun `get resources by ids and security - unauthorized`() {
        val entities = (1..3).map { repository.save(TestEntity(name = "TestEntity$it")) }

        every {
            mockSecurityFilter.canView(
                any(),
                principal
            )
        }.throws(ForbiddenException())

        shouldThrow<ForbiddenException> {
            securityService.getResourcesByIds(
                entities.map { it.id!! },
                principal = principal
            )
        }
    }

    @Test
    fun `get resources by IDs using filter`() {
        val entities = (1..3).map { repository.save(TestEntity(name = "TestEntity$it")) }
        val ids = "${entities[0].id},${entities[1].id}"

        service.listResources("id=in=($ids)").apply {
            count() shouldBe 2
        }
    }

    @Test
    fun `get resource by ids and projection`() {
        val entities = (1..3).map { repository.save(TestEntity(name = "TestEntity$it")) }

        service.getResourcesByIds(
            entities.map { it.id!! },
            clazz = MyView::class
        ).apply {
            count() shouldBe 3
            first().shouldBeInstanceOf<MyView>()
        }
    }

    @Test
    fun `delete resource by id`() {
        val entity = TestEntity(name = "test")
        repository.save(entity)

        service.deleteResourceById(entity.id!!)

        repository.findAll().apply {
            size shouldBe 0
        }
    }

    @Test
    fun `delete resource by id with security`() {
        val entity = TestEntity(name = "test")
        repository.save(entity)

        every { mockSecurityFilter.canView(entity, principal) }.returns(Unit)

        securityService.deleteResourceById(entity.id!!, principal = principal)

        repository.count() shouldBe 0

        verify(exactly = 1) { mockSecurityFilter.canView(entity, principal) }
    }

    @Test
    fun `delete resource by id with security - unauthorized`() {
        val entity = TestEntity(name = "test")
        repository.save(entity)

        every {
            mockSecurityFilter.canView(
                entity,
                principal
            )
        }.throws(ForbiddenException())

        shouldThrow<ForbiddenException> {
            securityService.deleteResourceById(entity.id!!, principal = principal)
        }

        repository.count() shouldBe 1
    }

    @Test
    fun `delete resources by id with security`() {
        val entities = (1..3).map { repository.save(TestEntity(name = "TestEntity$it")) }

        every { mockSecurityFilter.canView(any(), principal) }.returns(Unit)

        securityService.deleteResourcesByIds(
            entities.map { it.id!! },
            principal = principal
        )

        verify(exactly = 3) { mockSecurityFilter.canView(any(), principal) }

        repository.count() shouldBe 0
    }

    @Test
    fun `update ownership`() {
        val entity = TestEntityWithOwnership(
            name = "test",
            owner = UUID.randomUUID()
        )
        ownerRepository.save(entity)

        val newId = UUID.randomUUID()

        ownershipService.updateOwnership(entity.id!!, newId, principal)

        ownerRepository.findAll().first().apply {
            owner shouldBe newId
        }
    }
}
