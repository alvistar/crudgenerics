package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.Order
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OrderService : GenericService<Order, UUID>()
