package com.thealvistar.crudgenerics.services

import jakarta.annotation.PostConstruct

open class GenericServiceWithOwnershipEx<T : Any, ID : Any> :
    GenericService<T, ID>() {

    @PostConstruct
    fun setupSecurityFilter() {
        securityFilter = OwnershipSecurityFilterEx<T>(em, entityClass)
    }
}
