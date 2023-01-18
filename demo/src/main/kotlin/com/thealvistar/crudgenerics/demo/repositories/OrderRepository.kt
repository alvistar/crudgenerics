package com.thealvistar.crudgenerics.demo.repositories

import com.thealvistar.crudgenerics.demo.entities.Order
import com.thealvistar.crudgenerics.repositories.JpaExecutor
import java.util.UUID

interface OrderRepository : JpaExecutor<Order, UUID>
