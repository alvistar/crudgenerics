package com.thealvistar.crudgenerics.demo.entities

import com.thealvistar.crudgenerics.serializers.IdFlatSerDer
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    var name: String? = null,

    @ManyToOne(targetEntity = Category::class, fetch = FetchType.EAGER)
    @IdFlatSerDer
    var category: Category? = null,

    @ManyToMany(targetEntity = Category::class)
    var categories: List<Category>? = null,
)
