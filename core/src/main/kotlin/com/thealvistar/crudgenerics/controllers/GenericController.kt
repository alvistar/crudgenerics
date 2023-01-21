package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.dtos.UpdateOwnerDto
import com.thealvistar.crudgenerics.services.GenericService
import com.thealvistar.crudgenerics.utils.resolveGeneric
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal
import kotlin.reflect.KClass

val logger = mu.KotlinLogging.logger {}

/**
 * This is the base controller for all the controllers that will be created.
 * It contains all the basic CRUD operations.
 * @param T The entity class
 * @param D The DTO class
 * @param ID The ID class
 * @param P The projection interface
 */
abstract class GenericController<T : Any, ID : Any, D : Any, P : Any> : IGenericController<D, ID> {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    lateinit var service: GenericService<T, ID>

    @Suppress("UNCHECKED_CAST")
    private val projection: KClass<P> by lazy {
        (resolveGeneric<P>(this, GenericController::class, 3).kotlin)
    }

    override fun listResources(
        pageable: Pageable,
        filter: String?,
        principal: Principal?
    ): Page<P> {
        return service.listResources(filter, pageable, principal, projection)
    }

    override fun createResource(
        @RequestBody @Valid
        resourceDTO: D
    ): P {
        return service.createResource(resourceDTO, projection)
    }

    override fun getResourceById(@PathVariable id: ID, principal: Principal?) =
        service.getResourceById(id, principal, projection)

    override fun getResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?) =
        service.getResourcesByIds(ids, principal, projection)

    @PutMapping("/{id}")
    fun updateResourceById(
        @PathVariable id: ID,
        principal: Principal?,
        @RequestBody @Valid
        resourceDTO: D
    ) = service.updateResourceById(id, resourceDTO, principal, projection)

    override fun deleteResourceById(@PathVariable id: ID, principal: Principal?) =
        service.deleteResourceById(id, principal)

    @DeleteMapping
    override fun deleteResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?) =
        service.deleteResourcesByIds(ids, principal)

    @PutMapping("/{id}/ownership")
    override fun updateOwnership(
        @PathVariable id: ID,
        principal: Principal,
        @RequestBody dto: UpdateOwnerDto
    ) =
        service.updateOwnership(id, dto.owner, principal)
}
