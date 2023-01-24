package com.thealvistar.crudgenerics.services

import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired

open class GenericServiceWithOwnershipEx<T : Any, ID : Any> :
    GenericService<T, ID>() {

    @Autowired
    lateinit var em: EntityManager

    @PostConstruct
    fun setupSecurityFilter() {
        securityFilter = OwnershipSecurityFilterEx<T>(em, entityClass)
    }
}
