package com.thealvistar.crudgenerics.utils

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.RequestBody
import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springframework.web.method.HandlerMethod
import kotlin.reflect.KClass

data class CustomOperation(
    val handlerMethod: HandlerMethod,
    val requestBodyClass: KClass<*>,
)

fun List<CustomOperation>.get(
    handlerMethod: HandlerMethod,
): CustomOperation? {
    return this.firstOrNull {
        it.handlerMethod == handlerMethod.createWithResolvedBean()
    }
}

class CustomOperationCustomizer(val configuration: SpringDocConfigProperties) :
    OperationCustomizer {
    private val customOperations = mutableListOf<CustomOperation>()

    fun setRequestBodyClass(
        handlerMethod: HandlerMethod,
        requestBodyClass: KClass<*>,
    ) {
        customOperations.add(CustomOperation(handlerMethod, requestBodyClass))
    }

    override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
        val customOperation = customOperations.get(handlerMethod) ?: return operation

        val schemaName = if (configuration.isUseFqn) {
            customOperation.requestBodyClass.java.name
        } else {
            customOperation.requestBodyClass.java.simpleName
        }

        val schema = Schema<Any>().`$ref`(
            "#/components/schemas/$schemaName",
        )
        val requestBody = RequestBody()
        requestBody.content = Content().addMediaType(
            "application/json",
            MediaType().schema(schema),
        )
        operation.requestBody = requestBody

        return operation
    }
}
