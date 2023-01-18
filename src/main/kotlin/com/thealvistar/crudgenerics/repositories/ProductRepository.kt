package com.thealvistar.crudgenerics.repositories

import com.thealvistar.crudgenerics.entities.Product
import java.util.UUID

interface ProductRepository : JpaExecutor<Product, UUID>
