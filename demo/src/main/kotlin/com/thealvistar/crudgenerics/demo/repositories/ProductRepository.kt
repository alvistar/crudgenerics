package com.thealvistar.crudgenerics.demo.repositories

import com.thealvistar.crudgenerics.demo.entities.Product
import com.thealvistar.crudgenerics.repositories.JpaExecutor
import java.util.UUID

interface ProductRepository : JpaExecutor<Product, UUID>
