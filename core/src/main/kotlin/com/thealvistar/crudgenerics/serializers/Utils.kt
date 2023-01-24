package com.thealvistar.crudgenerics.serializers

import jakarta.persistence.Id
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

fun getIdField(clazz: KClass<*>) = clazz.memberProperties.find {
    it.javaField?.isAnnotationPresent(Id::class.java) ?: false
}

internal fun resolveIdField(clazz: KClass<*>): KProperty<*> {
    val field = getIdField(clazz) ?: clazz.memberProperties.find { it.name == "id" }

    return field ?: throw IllegalArgumentException("Unable to resolve id field for class $clazz")
}
