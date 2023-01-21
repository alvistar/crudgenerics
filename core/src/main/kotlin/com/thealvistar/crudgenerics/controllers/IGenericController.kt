package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.dtos.UpdateOwnerDto
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

interface IGenericController<D : Any, ID : Any> {
    @GetMapping(name = "listResources")
    fun listResources(pageable: Pageable, filter: String?, principal: Principal?): Page<out Any>

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteResourceById(@PathVariable id: ID, principal: Principal?)

    @GetMapping(params = ["id"])
    fun getResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?): List<Any>

    @GetMapping("/{id}")
    fun getResourceById(@PathVariable id: ID, principal: Principal?): Any

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createResource(
        @RequestBody @Valid
        resourceDTO: D
    ): Any

    @PutMapping("/{id}/ownership")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateOwnership(
        @PathVariable id: ID,
        principal: Principal,
        @RequestBody dto: UpdateOwnerDto
    )
}
