package com.thealvistar.crudgenerics.config

import com.github.javafaker.Faker
import com.thealvistar.crudgenerics.entities.Category
import com.thealvistar.crudgenerics.entities.Order
import com.thealvistar.crudgenerics.entities.Product
import com.thealvistar.crudgenerics.repositories.CategoryRepository
import com.thealvistar.crudgenerics.repositories.OrderRepository
import com.thealvistar.crudgenerics.repositories.ProductRepository
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class DbInit(
    val productRepository: ProductRepository,
    val orderRepository: OrderRepository,
    val categoryRepository: CategoryRepository
) {
    @PostConstruct
    fun dbInit() {
        val faker = Faker()

        val categories = (1..10).map {
            categoryRepository.save(Category(name = faker.food().fruit()))
        }

        val products = (1..10).map {
            productRepository.save(
                Product(
                    name = faker.pokemon().name(),
                    category = categories[it - 1]
                )
            )
        }

        val order = orderRepository.save(
            Order(
                products = productRepository.findAll()
            )
        )
    }
}
