package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.dtos.UpdateOwnerDto
import com.thealvistar.crudgenerics.services.GenericService
import jakarta.annotation.PostConstruct
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.pattern.PathPatternParser
import java.security.Principal
import kotlin.reflect.jvm.javaMethod

abstract class DtoGenericController<T : Any, ID : Any, D : Any>(
    service: GenericService<T, ID>? = null
) :
    IGenericController<D, ID> {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    private lateinit var autoService: GenericService<T, ID>

    @Autowired
    lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    open val service: GenericService<T, ID> by lazy {
        service ?: autoService
    }

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

    fun updateResourceById(
        @PathVariable id: ID,
        principal: Principal?,
        @RequestBody @Valid
        resourceDTO: D
    ): T =
        service.updateResourceById(id, resourceDTO, principal)

    @PostConstruct
    open fun setup() {
        // Get the @RequestMapping annotation from this class
        val requestMapping = this::class.java.getAnnotation(RequestMapping::class.java)

        // Register updateResourceById method to the requestMappingHandlerMapping
        requestMappingHandlerMapping.registerMapping(
            RequestMappingInfo.paths("${requestMapping.value[0]}/{id}")
                .methods(RequestMethod.PUT)
                .options(
                    RequestMappingInfo.BuilderConfiguration().apply {
                        patternParser = PathPatternParser()
                    }
                )
                .build(),
            this,
            this::updateResourceById.javaMethod!!
        )
    }
}
