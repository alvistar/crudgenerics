package com.thealvistar.crudgenerics.services

import org.springframework.data.jpa.domain.Specification
import java.security.Principal

interface SecurityFilter<T> {
    fun getSpecificationForList(principal: Principal): Specification<T>
    fun checkPermissions(resource: T, principal: Principal?)
}
