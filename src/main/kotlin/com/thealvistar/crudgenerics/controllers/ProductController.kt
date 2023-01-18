package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.entities.Product
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/products")
class ProductController : VerySimpleGenericController<Product, UUID>()
