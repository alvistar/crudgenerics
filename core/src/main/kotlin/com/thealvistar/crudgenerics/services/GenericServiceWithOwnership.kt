package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.Ownership

open class GenericServiceWithOwnership<T : Ownership, ID : Any> :
    GenericService<T, ID>(OwnershipSecurityFilter<T>())
