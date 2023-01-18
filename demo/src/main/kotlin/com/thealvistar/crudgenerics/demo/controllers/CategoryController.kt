package com.thealvistar.crudgenerics.demo.controllers

import com.thealvistar.crudgenerics.controllers.VerySimpleGenericController
import com.thealvistar.crudgenerics.demo.entities.Category
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/categories")
class CategoryController : VerySimpleGenericController<Category, UUID>()
