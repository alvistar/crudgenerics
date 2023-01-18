package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.dtos.UpdateOwnerDto
import com.thealvistar.crudgenerics.services.GenericService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
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

abstract class SimpleGenericController<T : Any, D : Any, ID : Any>() {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    lateinit var service: GenericService<T, ID>

    @GetMapping(name = "listResources")
    fun listResources(pageable: Pageable, filter: String?, principal: Principal?): Page<T> =
        service.listResources(filter, pageable, principal)

    @DeleteMapping
    fun deleteResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?) =
        service.deleteResourcesById(ids, principal)

    @DeleteMapping("/{id}")
    fun deleteResourceById(@PathVariable id: ID, principal: Principal?) =
        service.deleteResourceById(id, principal)

    @PutMapping("/{id}")
    fun updateResourceById(
        @PathVariable id: ID,
        principal: Principal?,
        @RequestBody @Valid
        resourceDTO: D
    ): T =
        service.updateResourceById(id, resourceDTO, principal)

    @GetMapping(params = ["id"])
    fun getResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?): List<T> =
        service.getResourcesByIds(ids, principal)

    @GetMapping("/{id}")
    fun getResourceById(@PathVariable id: ID, principal: Principal?): T =
        service.getResourceById(id, principal)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createResource(
        @RequestBody @Valid
        resourceDTO: D
    ): T = service.create(resourceDTO)

    @PutMapping("/{id}/ownership")
    fun updateOwnership(
        @PathVariable id: ID,
        principal: Principal,
        @RequestBody dto: UpdateOwnerDto
    ) =
        service.updateOwnership(id, dto.owner, principal)
}
