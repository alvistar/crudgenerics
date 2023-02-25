package com.thealvistar.crudgenerics.utils

import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.parameters.RequestBody
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

data class CustomOperation(
    val method: KFunction<*>,
    val tag: String,
    val requestBodyClass: KClass<*>,
)

fun List<CustomOperation>.get(
    operation: Operation,
    handlerMethod: HandlerMethod,
): CustomOperation? {
    return this.firstOrNull {
        it.method.javaMethod == handlerMethod.method &&
            operation.tags.getOrNull(0) == it.tag
    }
}

@Component
class CustomOperationCustomizer : OperationCustomizer {
    private val customOperations = mutableListOf<CustomOperation>()

    fun setRequestBodyClass(
        method: KFunction<*>,
        tag: String,
        requestBodyClass: KClass<*>,
    ) {
        customOperations.add(CustomOperation(method, tag, requestBodyClass))
    }

    override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
        val customOperation = customOperations.get(operation, handlerMethod) ?: return operation

        val schemas = ModelConverters.getInstance().read(customOperation.requestBodyClass.java)
        val schema = schemas.values.first()
        val requestBody = RequestBody()
        requestBody.content = Content().addMediaType(
            "application/json",
            MediaType().schema(schema),
        )
        operation.requestBody = requestBody
        operation.operationId = "updateResourceById"

        return operation
    }
}
