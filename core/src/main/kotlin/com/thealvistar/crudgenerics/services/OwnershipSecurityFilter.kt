package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.entities.Ownership
import com.thealvistar.crudgenerics.exceptions.ForbiddenException
import org.springframework.data.jpa.domain.Specification
import java.security.Principal
import java.util.UUID

class OwnershipSecurityFilter<T : Any> : SecurityFilter<T> {
    override fun getSpecificationForList(principal: Principal): Specification<T> {
        return Specification.where { root, _, cb ->
            cb.equal(root.get<Any>("owner"), UUID.fromString(principal.name))
        }
    }

    override fun afterGet(resource: T, principal: Principal?) {
        if (resource !is Ownership) {
            return
        }

        if (principal == null) {
            throw ForbiddenException()
        }

        if (resource.owner != UUID.fromString(principal.name)) {
            throw ForbiddenException()
        }
    }
}
