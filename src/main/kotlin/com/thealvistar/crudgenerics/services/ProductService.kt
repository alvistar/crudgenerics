package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.Product
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProductService : GenericService<Product, UUID>()
