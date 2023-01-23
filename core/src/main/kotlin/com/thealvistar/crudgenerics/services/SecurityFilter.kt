package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.exceptions.ForbiddenException
import org.springframework.data.jpa.domain.Specification
import java.security.Principal

interface SecurityFilter<T> {
    fun getSpecificationForList(principal: Principal): Specification<T>

    @Throws(ForbiddenException::class)
    fun afterGet(resource: T, principal: Principal?) {}

    @Throws(ForbiddenException::class)
    fun beforeCreate(resource: T, principal: Principal?) {}

    @Throws(ForbiddenException::class)
    fun beforeUpdate(resource: T, principal: Principal?) {}
}
