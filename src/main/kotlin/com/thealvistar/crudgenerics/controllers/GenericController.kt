package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.dtos.UpdateOwnerDto
import com.thealvistar.crudgenerics.entities.Ownership
import com.thealvistar.crudgenerics.services.GenericService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.security.Principal

val logger = mu.KotlinLogging.logger {}

abstract class GenericController<T : Ownership, D : Any, ID : Any, P : Any>(
    private val projection: Class<P>
) {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    lateinit var service: GenericService<T, ID>

    @GetMapping(name = "listResources")
    fun listResources(
        pageable: Pageable,
        filter: String?,
        principal: Principal
    ): Page<P> {
        return service.listResources(filter, pageable, principal, projection)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createResource(
        @RequestBody @Valid
        resourceDTO: D
    ): P {
        return service.create(resourceDTO, projection)
    }

    @GetMapping("/{id}")
    fun getResourceById(@PathVariable id: ID, principal: Principal) =
        service.getResourceById(id, principal, projection)

    @GetMapping(params = ["id"])
    fun getResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal) =
        service.getResourcesByIds(ids, principal, projection)

    @PutMapping("/{id}")
    fun updateResourceById(
        @PathVariable id: ID,
        principal: Principal,
        @RequestBody @Valid
        resourceDTO: D
    ) = service.updateResourceById(id, resourceDTO, principal, projection)

    @DeleteMapping("/{id}")
    fun deleteResourceById(@PathVariable id: ID, principal: Principal) =
        service.deleteResourceById(id, principal)

    @DeleteMapping
    fun deleteResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal) =
        service.deleteResourcesById(ids, principal)

    @PutMapping("/{id}/ownership")
    fun updateOwnership(
        @PathVariable id: ID,
        principal: Principal,
        @RequestBody dto: UpdateOwnerDto
    ) =
        service.updateOwnership(id, dto.owner, principal)
}
