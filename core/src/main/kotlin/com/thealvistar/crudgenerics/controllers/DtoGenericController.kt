package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.dtos.UpdateOwnerDto
import com.thealvistar.crudgenerics.services.GenericService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal

abstract class DtoGenericController<T : Any, ID : Any, D : Any>() :
    IGenericController<D, ID> {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    lateinit var service: GenericService<T, ID>

    override fun listResources(
        pageable: Pageable,
        filter: String?,
        principal: Principal?
    ): Page<T> =
        service.listResources(filter, pageable, principal)

    override fun deleteResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?) =
        service.deleteResourcesByIds(ids, principal)

    override fun deleteResourceById(@PathVariable id: ID, principal: Principal?) =
        service.deleteResourceById(id, principal)

    override fun updateResourceById(
        @PathVariable id: ID,
        principal: Principal?,
        @RequestBody @Valid
        resourceDTO: D
    ): T =
        service.updateResourceById(id, resourceDTO, principal)

    override fun getResourcesByIds(
        @RequestParam("id") ids: List<ID>,
        principal: Principal?
    ): List<T> =
        service.getResourcesByIds(ids, principal)

    override fun getResourceById(@PathVariable id: ID, principal: Principal?): T =
        service.getResourceById(id, principal)

    override fun createResource(
        @RequestBody @Valid
        resourceDTO: D
    ): T = service.createResource(resourceDTO)

    override fun updateOwnership(
        @PathVariable id: ID,
        principal: Principal,
        @RequestBody dto: UpdateOwnerDto
    ) =
        service.updateOwnership(id, dto.owner, principal)
}
