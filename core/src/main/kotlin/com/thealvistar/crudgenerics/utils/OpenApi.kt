package com.thealvistar.crudgenerics.utils

import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.parameters.RequestBody
import org.springdoc.core.customizers.OperationCustomizer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

fun requestBodyCustomizer(method: KFunction<*>, entityClass: KClass<*>): OperationCustomizer {
    return OperationCustomizer { operation, handlerMethod ->
        if (handlerMethod.method == method.javaMethod!!) {
            // convert entity class to openapi schema taking care of jackson annotations

            val schemas = ModelConverters.getInstance().read(entityClass.java)
            val schema = schemas.values.first()
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

inline fun <reified T : Any> requestBodyCustomizer(method: KFunction<*>) =
    requestBodyCustomizer(method, T::class)
