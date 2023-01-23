package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.services.GenericService
import com.thealvistar.crudgenerics.utils.requestBodyCustomizer
import io.swagger.v3.oas.annotations.Parameter
import jakarta.annotation.PostConstruct
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.http.RequestEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import java.security.Principal
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

interface UpdateMixinFromEntity<T : Any, ID : Any, P : Any> {
    val service: GenericService<T, ID>
    val projection: KClass<P>
    val entityClass: KClass<T>
    val applicationContext: GenericApplicationContext

    /*
        * Update resource by id
        * This version takes the whole entity as a json string
        * This enables to merge the request entity into existing one
        * If we use a dto there is no way to know which fields are missing
     */
    @PutMapping("/{id}")
    fun updateResourceByIdJson(
        @PathVariable id: ID,
        principal: Principal?,
        @Parameter(hidden = true) requestEntity: RequestEntity<String>
    ) = service.updateResourceById(id, requestEntity.body!!, principal, projection)

    // Customize current openapi operation
    @PostConstruct
    fun registerBeans() {
        val method = this::class.members.filterIsInstance<KFunction<*>>()
            .first { it.name == "updateResourceByIdJson" }

        applicationContext.registerBean(
            "${entityClass.simpleName}OperationCustomizer"
        ) {
            requestBodyCustomizer(method, entityClass)
        }
    }
}
