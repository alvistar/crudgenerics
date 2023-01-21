package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.utils.resolveGeneric
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.parameters.RequestBody
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.http.RequestEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import java.security.Principal
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

abstract class BasicGenericController<T : Any, ID : Any> : DtoGenericController<T, ID, T>() {
    @Suppress("UNCHECKED_CAST")
    private val entityClass: KClass<T> by lazy {
        resolveGeneric<T>(
            this,
            BasicGenericController::class,
            0
        ).kotlin
    }

    @Suppress("UNCHECKED_CAST")
    private val idClass: KClass<T> by lazy {
        resolveGeneric<T>(
            this,
            BasicGenericController::class,
            1
        ).kotlin
    }

    val updateOperationId = "update${entityClass.simpleName}ById"

    @PutMapping("/{id}")
    fun updateResourceByIdJson(
        @PathVariable id: ID,
        principal: Principal?,
        @Parameter(hidden = true) requestEntity: RequestEntity<String>
    ) = service.updateResourceById(id, requestEntity.body!!, principal)

    override fun setup() {
    }

    // Customize current openapi operation
    @Bean
    fun updateOperationCustomizer(): OperationCustomizer {
        // get reference to updateResourceById method
        val method = this::class.members.filterIsInstance<KFunction<*>>()
            .first { it.name == "updateResourceByIdJson" }

        return OperationCustomizer { operation, handlerMethod ->
            if (handlerMethod.method == method.javaMethod!!) {
                val schema = ModelConverters.getInstance().read(entityClass.java).values.first()
                val requestBody = RequestBody()
                requestBody.content = Content().addMediaType(
                    "application/json",
                    MediaType().schema(schema)
                )
                operation.requestBody = requestBody
                operation.operationId = "updateResourceById"
            }
            operation
        }
    }
}
