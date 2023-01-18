package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.entities.Category
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/categories")
class CategoryController : VerySimpleGenericController<Category, UUID>()
