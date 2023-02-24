package com.thealvistar.crudgenerics.controllers

import com.thealvistar.crudgenerics.utils.requestBodyCustomizer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.Locale
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

fun RequestMappingHandlerMapping.unregisterMethod(method: KFunction<*>) {
    val mappingInfo = handlerMethods
        .filter { it.value.method == method.javaMethod }.keys.first()

    unregisterMapping(mappingInfo)
}

fun GenericApplicationContext.setRequestBody(
    method: KFunction<*>,
    dtoClass: KClass<*>,
    simpleClassName: String,
) {
    // Convert simpleClassName to kebab-case
    val operationId = simpleClassName
        .replace("([A-Z])".toRegex(), "-$1")
        .lowercase(Locale.getDefault())
        .removePrefix("-")

    registerBean("${simpleClassName}operationCustomizer") {
        requestBodyCustomizer(
            method,
            dtoClass,
            operationId,
        )
    }
}
