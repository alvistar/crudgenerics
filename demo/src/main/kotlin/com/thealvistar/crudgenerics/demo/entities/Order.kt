package com.thealvistar.crudgenerics.demo.entities

import com.thealvistar.crudgenerics.serializers.IdFlatSerDerCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "_order")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToMany(targetEntity = Product::class)
    @IdFlatSerDerCollection
    var products: List<Product>? = null
)
