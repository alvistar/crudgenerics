package com.thealvistar.crudgenerics.demo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.thealvistar.crudgenerics.entities.Category
import com.thealvistar.crudgenerics.entities.Product
import org.junit.jupiter.api.Test
import java.util.UUID

class ProductEntityTest {
    @Test
    fun serialization() {
        val category = Category(name = "Fruits", id = UUID.randomUUID())
        val product = Product(name = "Apple", category = category)
        val om = jacksonObjectMapper()

        println(listOf(om.writeValueAsString(product)))
    }
}
