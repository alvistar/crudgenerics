package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.TestEntity
import com.thealvistar.crudgenerics.entities.TestEntityWithOwnership
import com.thealvistar.crudgenerics.repositories.TestRepository
import com.thealvistar.crudgenerics.repositories.TestWithOwnershipRepository
import io.github.perplexhub.rsql.RSQLJPAAutoConfiguration
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor
import java.security.Principal
import java.util.UUID

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(RSQLJPAAutoConfiguration::class)
class RSQLFilterTest(
    val repository: TestRepository,
    val repositoryWithOwnership: TestWithOwnershipRepository,
) {
    @Test
    fun `test filter resources`() {
        val rsqlFilter = RSQLFilter(repository)

        val entities = (1..3).map {
            val e = TestEntity(name = "test$it")
            repository.save(e)
        }

        rsqlFilter.filterResources(filter = "name==test1").apply {
            size shouldBe 1
            get().toList()[0].name shouldBe "test1"
        }
    }

    @Test
    fun `test security filter`() {
        val principal = mockk<Principal>()
        val johnId = UUID.randomUUID()

        every { principal.name } returns johnId.toString()

        val mock = spyk<OwnershipSecurityFilter<TestEntityWithOwnership>>()

        val rsqlFilter = RSQLFilter(repositoryWithOwnership, mock)

        val entities = (1..3).map {
            TestEntityWithOwnership(name = "test$it", owner = johnId)
        }

        entities[0].owner = UUID.randomUUID()
        repositoryWithOwnership.saveAll(entities)

        rsqlFilter.filterResources(principal = principal).apply {
            size shouldBe 2
        }

        verify { mock.getSpecificationForList(principal) }
    }
}
