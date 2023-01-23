package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.TestEntity
import com.thealvistar.crudgenerics.entities.TestEntityWithOwnership
import com.thealvistar.crudgenerics.exceptions.ForbiddenException
import com.thealvistar.crudgenerics.repositories.TestWithOwnershipRepository
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.TestConstructor
import java.security.Principal
import java.util.UUID

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class OwnershipSecurityFilterExTest(
    val em: EntityManager,
    val repository: TestWithOwnershipRepository
) {
    @Test
    fun initTest() {
        val securityFilter = OwnershipSecurityFilterEx(em, TestEntity::class)
        securityFilter.relationships.apply {
            this?.size shouldBe 1
        }
    }

    @Test
    fun canCreate() {
        val securityFilter = OwnershipSecurityFilterEx(em, TestEntity::class)
        val johnId = UUID.randomUUID()
        val aliceID = UUID.randomUUID()

        val estServer = TestEntityWithOwnership().apply {
            owner = johnId
            repository.save(this)
        }

        val iotSvc = TestEntity().apply {
            this.reference = estServer
        }

        shouldNotThrow<ForbiddenException> {
            securityFilter.canCreate(iotSvc, Principal { johnId.toString() })
        }

        shouldThrow<ForbiddenException> {
            securityFilter.canCreate(iotSvc, Principal { aliceID.toString() })
        }
    }
}
