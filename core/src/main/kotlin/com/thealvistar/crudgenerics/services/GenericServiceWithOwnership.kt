package com.thealvistar.crudgenerics.services

open class GenericServiceWithOwnership<T : Any, ID : Any> :
    GenericService<T, ID>(OwnershipSecurityFilter<T>())
