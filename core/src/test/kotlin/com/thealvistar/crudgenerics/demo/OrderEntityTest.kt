package com.thealvistar.crudgenerics.demo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.thealvistar.crudgenerics.entities.Category
import com.thealvistar.crudgenerics.entities.Order
import com.thealvistar.crudgenerics.entities.Product
import org.junit.jupiter.api.Test
import java.util.UUID

class OrderEntityTest {
    @Test
    fun `serialization`() {
        val order = Order(
            id = UUID.randomUUID(),
            products = listOf(
                Product(
                    id = UUID.randomUUID(),
                    name = "Apple",
                    category = Category(
                        id = UUID.randomUUID(),
                        name = "Fruits"
                    )
                )
            )
        )

        val om = jacksonObjectMapper()

        println(om.writeValueAsString(order))

        val o = om.readValue<Order>(om.writeValueAsString(order))
        print(o)
    }
}
