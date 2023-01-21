package com.thealvistar.crudgenerics.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import org.springframework.context.annotation.Profile
import java.util.UUID

@Entity
@Profile("test")
class TestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    var name: String? = null,

    @OneToOne
    var reference: TestEntityWithOwnership? = null
)
