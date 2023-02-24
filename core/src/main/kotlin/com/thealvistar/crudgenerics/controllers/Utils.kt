package com.thealvistar.crudgenerics.controllers

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

fun RequestMappingHandlerMapping.unregisterMethod(method: KFunction<*>) {
    val mappingInfo = handlerMethods
        .filter { it.value.method == method.javaMethod }.keys.first()

    unregisterMapping(mappingInfo)
}
