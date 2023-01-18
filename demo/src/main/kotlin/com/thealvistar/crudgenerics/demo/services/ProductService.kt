package com.thealvistar.crudgenerics.demo.services

import com.thealvistar.crudgenerics.demo.entities.Product
import com.thealvistar.crudgenerics.services.GenericService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProductService : GenericService<Product, UUID>()
