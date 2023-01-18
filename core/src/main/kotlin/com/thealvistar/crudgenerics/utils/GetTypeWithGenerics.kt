package com.thealvistar.crudgenerics.utils

import org.springframework.core.ResolvableType

fun <T> List<Any>.getTypeWithGenerics(clazz: Class<T>, vararg generic: Class<*>): List<T> {
    return this.filter {
        if (!clazz.isAssignableFrom(it::class.java)) {
            return@filter false
        }

        val resolvableType = ResolvableType.forInstance(it).`as`(clazz)
        if (resolvableType.hasUnresolvableGenerics()) {
            return@filter false
        }

        val generics = ResolvableType.forInstance(it).`as`(clazz).generics
        // Check if generics match the ones in param generic
        if (generics.size == generic.size) {
            for (i in generics.indices) {
                val testType = ResolvableType.forClass(generic[i])

                if (!generics[i].isAssignableFrom(generic[i])) {
                    return@filter false
                }
            }
            return@filter true
        }

        false
    }.map {
        @Suppress("UNCHECKED_CAST")
        it as T
    }
}
