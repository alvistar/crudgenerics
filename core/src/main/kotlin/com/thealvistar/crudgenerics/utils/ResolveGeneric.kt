package com.thealvistar.crudgenerics.utils

import org.springframework.core.ResolvableType
import kotlin.reflect.KClass

fun <T> resolveGeneric(instance: Any, asType: KClass<*>, argIndex: Int): Class<T> {
    val type = ResolvableType.forInstance(instance).`as`(asType.java)

    if (type.hasUnresolvableGenerics()) {
        throw IllegalArgumentException("Unable to resolve generics  $type")
    }

    val genericType = type.getGeneric(argIndex)
    if (genericType == ResolvableType.NONE) {
        throw IllegalArgumentException("Unable to resolve generics  $type for index $argIndex")
    }

    @Suppress("UNCHECKED_CAST")
    return genericType.resolve() as Class<T>
}

fun getGenerics(clazz: KClass<*>, asType: KClass<*>): List<KClass<*>> {
    val type = ResolvableType.forClass(clazz.java).`as`(asType.java)

    if (type.hasUnresolvableGenerics()) {
        throw IllegalArgumentException("Unable to resolve generics  $type")
    }

    return type.generics.map {
        it.resolve()?.kotlin ?: throw IllegalArgumentException("Unable to resolve generics  $type")
    }
}
