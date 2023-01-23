package com.thealvistar.crudgenerics.controllers

// Class that implements the basic CRUD operations for a generic entity
// it does not require a DTO for creating or updating the entity
abstract class PjDtoGenericController<T : Any, ID : Any, D : Any, P : Any> :
    PjBaseGenericController<T, ID, D, P>(), UpdateMixinFromDto<T, ID, D, P>
