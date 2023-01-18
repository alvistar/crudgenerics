package com.thealvistar.crudgenerics.demo.repositories

import com.thealvistar.crudgenerics.demo.entities.Category
import com.thealvistar.crudgenerics.repositories.JpaExecutor
import java.util.UUID

interface CategoryRepository : JpaExecutor<Category, UUID>
