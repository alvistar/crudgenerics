package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.exceptions.ForbiddenException
import org.springframework.data.jpa.domain.Specification
import java.security.Principal

interface SecurityFilter<T> {
    fun getSpecificationForList(principal: Principal): Specification<T>

    @Throws(ForbiddenException::class)
    fun checkPermissions(resource: T, principal: Principal?)
}
