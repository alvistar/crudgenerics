package com.thealvistar.crudgenerics.services

import com.thealvistar.crudgenerics.repositories.JpaExecutor
import io.github.perplexhub.rsql.RSQLJPASupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import java.security.Principal
import java.util.UUID


fun <T> ownershipSecurityFilter(principal: Principal): Specification<T> {
    return Specification.where { root, _, cb ->
        cb.equal(root.get<Any>("owner"), UUID.fromString(principal.name))
    }
}


class RSQLFilter<T : Any, ID : Any>(
    private val repository: JpaExecutor<T, ID>,
    private val securityFilter: SecurityFilter<T>? = null
) {

    private fun getListSpecification(
        filter: String? = null,
        principal: Principal? = null
    ): Specification<T> {
        val filterSpecification: Specification<T> = RSQLJPASupport.toSpecification(filter)

        return if (securityFilter != null && principal != null) {
            val ownerSpecification = securityFilter.getSpecificationForList(principal)

            filterSpecification.and(ownerSpecification)
        } else {
            filterSpecification
        }
    }

    fun filterResources(
        filter: String? = null,
        pageable: Pageable = Pageable.unpaged(),
        principal: Principal? = null
    ): Page<T> {
        val specification = getListSpecification(filter, principal)

        return repository.findAll(specification, pageable)
    }

    fun <P> filterResourcesProjection(
        projection: Class<P>,
        filter: String? = null,
        pageable: Pageable = Pageable.unpaged(),
        principal: Principal? = null
    ): Page<P> {
        val specification = getListSpecification(filter, principal)
        return repository.findBy<T, Page<P>>(specification) {
            it.`as`(projection).page(pageable)
        }
    }
}
