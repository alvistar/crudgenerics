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
import org.springframework.core.convert.ConversionService
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
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.security.Principal
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

data class Prova(val name: String, val age: Int)

abstract class PageProva : Page<Prova>

/**
 * Basic generic controller using entity for body requests and DTO for response.
 * It contains all the basic CRUD operations.
 * @param T The entity class
 * @param D The DTO class
 * @param ID The ID class
 */
@OptIn(ExperimentalStdlibApi::class)
@Suppress("UNCHECKED_CAST")
abstract class DtoGenericController<T : Any, ID : Any, D : Any>(
    service: GenericService<T, ID>? = null,
) {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    private lateinit var autoService: GenericService<T, ID>

    @Autowired
    lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    @Autowired
    lateinit var conversionService: ConversionService

    @Autowired
    lateinit var applicationContext: GenericApplicationContext

    @Autowired
    lateinit var om: ObjectMapper

    @Autowired
    lateinit var validator: Validator

    val entityClass: KClass<T>
    val idClass: KClass<ID>
    val dtoClass: KClass<D>
//    val projectionClass: KClass<P>

    init {
        val generics = getGenerics(this::class, DtoGenericController::class)
        entityClass = generics[0] as KClass<T>
        idClass = generics[1] as KClass<ID>
        dtoClass = generics[2] as KClass<D>
    }

    open val service: GenericService<T, ID> by lazy {
        service ?: autoService
    }

    private fun <R : Any> ServerRequest.pathVariableAs(name: String, clazz: KClass<R>): R {
        val pathVariable = pathVariable(name)
        return conversionService.convert(pathVariable, clazz.java)
            ?: throw IllegalArgumentException("Cannot convert $pathVariable to ${clazz.java}")
    }

    @GetMapping
    fun listResources(pageable: Pageable, filter: String?, principal: Principal?): Page<T> =
        service.listResources(filter, pageable, principal)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?) =
        service.deleteResourcesByIds(ids, principal)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteResourceById(@PathVariable id: ID, principal: Principal?) =
        service.deleteResourceById(id, principal)

    @GetMapping("/{id}")
    fun getResourceById(@PathVariable id: ID, principal: Principal?) =
        service.getResourceById(id, principal)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createResource(
        principal: Principal?,
        @RequestBody @Valid
        resourceDTO: D,
    ): T = service.createResource(resourceDTO, principal)

    @PutMapping("/{id}/ownership")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateOwnership(
        @PathVariable id: ID,
        principal: Principal,
        @RequestBody dto: UpdateOwnerDto,
    ) =
        service.updateOwnership(id, dto.owner, principal)

    @PutMapping("/{id}")
    fun updateResourceById(
        @PathVariable id: ID,
        principal: Principal?,
        @Parameter(hidden = true) requestEntity: RequestEntity<String>,
    ): T {
        return if (dtoClass == entityClass) {
            // Use plain json
            service.updateResourceById(id, requestEntity.body!!, principal)
        } else {
            // Covert first to DTO class so we can validate it
            val dto = om.readValue(requestEntity.body!!, dtoClass.java)
            validator.validate(dto).throwIfNotEmpty()
            service.updateResourceById(id, dto, principal)
        }
    }

    @PostConstruct
    fun setup() {
        val method = this::class.members.filterIsInstance<KFunction<*>>()
            .first { it.name == "updateResourceById" }

        applicationContext.registerBean("${this::class.simpleName}operationCustomizer") {
            requestBodyCustomizer(
                method,
                dtoClass,
            )
        }
    }
}
