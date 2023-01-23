package com.thealvistar.crudgenerics.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.GenericApplicationContext

// Class that implements the basic CRUD operations for a generic entity
// it does not require a DTO for creating or updating the entity
abstract class PjGenericController<T : Any, ID : Any, P : Any> :
    PjBaseGenericController<T, ID, T, P>(), UpdateMixinFromEntity<T, ID, P> {
    @Autowired
    override lateinit var applicationContext: GenericApplicationContext
}
