package com.thealvistar.crudgenerics.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.thealvistar.crudgenerics.entities.Category
import com.thealvistar.crudgenerics.entities.Product
import com.thealvistar.crudgenerics.repositories.CategoryRepository
import com.thealvistar.crudgenerics.repositories.ProductRepository
import io.github.perplexhub.rsql.RSQLJPAAutoConfiguration
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(RSQLJPAAutoConfiguration::class)
@AutoConfigureJson
class SerializationTest(
    val productRepository: ProductRepository,
    val categoryRepository: CategoryRepository,
    val om: ObjectMapper
) {
    @Test
    fun `test serialization`() {
        val category = Category(name = "Fruits")
        categoryRepository.save(category)
        val product = Product(name = "Apple", category = category)
        productRepository.save(product)
        val serialized = om.writeValueAsString(product)
        print(serialized)

        val result = om.readValue<Product>(serialized)

        result.category?.id shouldBe category.id
    }
}
