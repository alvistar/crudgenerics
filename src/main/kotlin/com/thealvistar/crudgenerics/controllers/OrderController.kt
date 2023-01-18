package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.entities.Order
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/orders")
class OrderController : VerySimpleGenericController<Order, UUID>()
