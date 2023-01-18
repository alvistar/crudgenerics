package com.thealvistar.crudgenerics.repositories

import com.thealvistar.crudgenerics.entities.Order
import java.util.UUID

interface OrderRepository : JpaExecutor<Order, UUID>
