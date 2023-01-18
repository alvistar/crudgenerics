package com.thealvistar.crudgenerics.repositories

import com.thealvistar.crudgenerics.entities.Category
import java.util.UUID

interface CategoryRepository : JpaExecutor<Category, UUID>
