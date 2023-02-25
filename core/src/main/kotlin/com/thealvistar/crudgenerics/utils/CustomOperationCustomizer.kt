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

@Component
class CustomOperationCustomizer : OperationCustomizer {
    private val customOperations = mutableListOf<CustomOperation>()

    fun setRequestBodyClass(
        handlerMethod: HandlerMethod,
        requestBodyClass: KClass<*>,
    ) {
        customOperations.add(CustomOperation(handlerMethod, requestBodyClass))
    }

    override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
        val customOperation = customOperations.get(handlerMethod) ?: return operation

        val schemas = ModelConverters.getInstance().read(customOperation.requestBodyClass.java)
        val schema = schemas.values.first()
        val requestBody = RequestBody()
        requestBody.content = Content().addMediaType(
            "application/json",
            MediaType().schema(schema),
        )
        operation.requestBody = requestBody

        return operation
    }
}
