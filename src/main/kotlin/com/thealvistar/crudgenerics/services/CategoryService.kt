package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.Category
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CategoryService : GenericService<Category, UUID>()
