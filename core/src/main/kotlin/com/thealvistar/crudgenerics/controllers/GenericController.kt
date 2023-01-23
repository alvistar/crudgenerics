package com.thealvistar.crudgenerics.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.thealvistar.crudgenerics.dtos.UpdateOwnerDto
import com.thealvistar.crudgenerics.services.GenericService
import com.thealvistar.crudgenerics.utils.getGenerics
import com.thealvistar.crudgenerics.utils.requestBodyCustomizer
import com.thealvistar.crudgenerics.utils.throwIfNotEmpty
import io.swagger.v3.oas.annotations.Parameter
import jakarta.annotation.PostConstruct
import jakarta.validation.Valid
import jakarta.validation.Validator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.security.Principal
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

val logger = mu.KotlinLogging.logger {}

/**
 * This is the base controller for all the controllers that will be created.
 * It contains all the basic CRUD operations.
 * @param T The entity class
 * @param D The DTO class
 * @param ID The ID class
 * @param P The projection interface
 */
@Suppress("UNCHECKED_CAST")
abstract class GenericController<T : Any, ID : Any, D : Any, P : Any> {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    lateinit var service: GenericService<T, ID>

    @Autowired
    lateinit var validator: Validator

    @Autowired
    lateinit var om: ObjectMapper

    @Autowired
    lateinit var applicationContext: GenericApplicationContext

    val entityClass: KClass<T>
    val idClass: KClass<ID>
    val dtoClass: KClass<D>
    val projectionClass: KClass<P>

    init {
        val generics = getGenerics(this::class, GenericController::class)
        entityClass = generics[0] as KClass<T>
        idClass = generics[1] as KClass<ID>
        dtoClass = generics[2] as KClass<D>
        projectionClass = generics[3] as KClass<P>
    }

    @GetMapping
    fun listResources(
        pageable: Pageable,
        filter: String?,
        principal: Principal?
    ): Page<P> {
        return service.listResources(filter, pageable, principal, projectionClass)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createResource(
        @RequestBody @Valid
        resourceDTO: D
    ): P {
        return service.createResource(resourceDTO, projectionClass)
    }

    @GetMapping("/{id}")
    fun getResourceById(@PathVariable id: ID, principal: Principal?) =
        service.getResourceById(id, principal, projectionClass)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteResourceById(@PathVariable id: ID, principal: Principal?) =
        service.deleteResourceById(id, principal)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?) =
        service.deleteResourcesByIds(ids, principal)

    @PutMapping("/{id}/ownership")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateOwnership(
        @PathVariable id: ID,
        principal: Principal,
        @RequestBody dto: UpdateOwnerDto
    ) =
        service.updateOwnership(id, dto.owner, principal)

    @PutMapping("/{id}")
    fun updateResourceById(
        @PathVariable id: ID,
        principal: Principal?,
        @Parameter(hidden = true) requestEntity: RequestEntity<String>
    ): P {
        return if (dtoClass == entityClass) {
            // Use plain json
            service.updateResourceById(id, requestEntity.body!!, principal, projectionClass)
        } else {
            // Covert first to DTO class so we can validate it
            val dto = om.readValue(requestEntity.body!!, dtoClass.java)
            validator.validate(dto).throwIfNotEmpty()
            service.updateResourceById(id, dto, principal, projectionClass)
        }
    }

    @PostConstruct
    fun setup() {
        val method = this::class.members.filterIsInstance<KFunction<*>>()
            .first { it.name == "updateResourceById" }

        applicationContext.registerBean("${this::class.simpleName}operationCustomizer") {
            requestBodyCustomizer(
                method,
                dtoClass
            )
        }
    }
}
