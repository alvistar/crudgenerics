package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.services.GenericService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.security.Principal
import kotlin.reflect.KClass

interface UpdateMixinFromDto<T : Any, ID : Any, D : Any, P : Any> {
    val service: GenericService<T, ID>
    val projection: KClass<P>

    @PutMapping("/{id}")
    fun updateResourceById(
        @PathVariable id: ID,
        principal: Principal?,
        @RequestBody @Valid
        resourceDTO: D
    ) = service.updateResourceById(id, resourceDTO, principal, projection)
}
