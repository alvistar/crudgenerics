package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.Ownership
import jakarta.annotation.PostConstruct

open class GenericServiceWithOwnershipEx<T : Ownership, ID : Any> :
    GenericService<T, ID>() {

    @PostConstruct
    fun setupSecurityFilter() {
        securityFilter = OwnershipSecurityFilterEx<T>(em, entityClass)
    }
}
