package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.dtos.UpdateOwnerDto
import com.thealvistar.crudgenerics.services.GenericService
import com.thealvistar.crudgenerics.utils.camelToKebabCase
import com.thealvistar.crudgenerics.utils.getGenerics
import io.swagger.v3.oas.annotations.Operation
import jakarta.annotation.PostConstruct
import jakarta.validation.Valid
import org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder
import org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder
import org.springdoc.webmvc.core.fn.SpringdocRouteBuilder.route
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.convert.ConversionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.security.Principal
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

@OptIn(ExperimentalStdlibApi::class)
@Suppress("UNCHECKED_CAST")
abstract class DtoGenericController<T : Any, ID : Any, D : Any>(
    service: GenericService<T, ID>? = null
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

    val entityClass: KClass<T>
    val idClass: KClass<ID>
    val dtoClass: KClass<D>

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

    @GetMapping()
    fun listResources(
        pageable: Pageable,
        filter: String?,
        principal: Principal?
    ): Page<T> =
        service.listResources(filter, pageable, principal)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteResourcesByIds(@RequestParam("id") ids: List<ID>, principal: Principal?) =
        service.deleteResourcesByIds(ids, principal)

    fun deleteResourceById(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariableAs("id", idClass)
        val principal = serverRequest.principal().getOrNull()
        service.deleteResourceById(id, principal)
        return ServerResponse.noContent().build()
    }

    @GetMapping(params = ["id"])
    fun getResourcesByIds(
        @RequestParam("id") ids: List<ID>,
        principal: Principal?
    ): List<T> =
        service.getResourcesByIds(ids, principal)

    @ResponseStatus(HttpStatus.OK)
    fun getResourceById(severRequest: ServerRequest): ServerResponse {
        val id = severRequest.pathVariableAs("id", idClass)
        val principal = severRequest.principal().getOrNull()
        val resource = service.getResourceById(id, principal)
        return ServerResponse.ok().body(resource)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createResource(
        @RequestBody @Valid
        resourceDTO: D
    ): T = service.createResource(resourceDTO)

    @PutMapping("/{id}/ownership")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateOwnership(
        @PathVariable id: ID,
        principal: Principal,
        @RequestBody dto: UpdateOwnerDto
    ) =
        service.updateOwnership(id, dto.owner, principal)

    @Operation(operationId = "updateFranco")
    fun updateResourceById(
        severRequest: ServerRequest
    ): ServerResponse {
        // Get the id parameter and convert to type ID

        val id = severRequest.pathVariable("id")
        val idConverted = conversionService.convert(id, idClass.java) as ID
        val principal = severRequest.principal().getOrNull()

        val resourceDTO: Any = if (entityClass == dtoClass) {
            severRequest.body(String::class.java)
        } else {
            severRequest.body(dtoClass.java)
        }

        val resource = service.updateResourceById(idConverted, resourceDTO, principal)
        return ServerResponse.ok().body(resource)
    }

    @PostConstruct
    fun registerRouterFunction() {
        val requestMapping = this::class.java.getAnnotation(RequestMapping::class.java)
        val tag = this::class.simpleName?.camelToKebabCase()
        val responseWithEntity = responseBuilder()
            .responseCode("200")
            .implementation(entityClass.java)

        val route = route()
            .GET("${requestMapping.value[0]}/{id}", this::getResourceById) {
                it.operationId("getResourceById")
                    .tag(tag)
                    .response(responseWithEntity)
            }
            .PUT("${requestMapping.value[0]}/{id}", this::updateResourceById) {
                it.operationId("updateResourceById")
                    .tag(tag)
                    .requestBody(requestBodyBuilder().implementation(entityClass.java))
                    .response(responseWithEntity)
            }
            .DELETE("${requestMapping.value[0]}/{id}", this::deleteResourceById) {
                it.operationId("deleteResourceById")
                    .tag(tag)
                    .response(responseBuilder().responseCode("204"))
            }
            .build()

        applicationContext.registerBean("${this::class.simpleName}RouterFunction") { route }
    }
}
