package com.thealvistar.crudgenerics.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

/**
 * Deserialize an object from a JSON representing the id field.
 */

class IdFlatDeserializer<T : Any>(clazz: Class<T>? = null, property: BeanProperty? = null) :
    JsonDeserializer<T>(), ContextualDeserializer {

    private lateinit var clazz: Class<T>

    override fun createContextual(
        ctxt: DeserializationContext,
        property: BeanProperty
    ): JsonDeserializer<*> {
        @Suppress("UNCHECKED_CAST")
        // Check if are dealing with collection
        clazz = (property.type.contentType?.rawClass ?: property.type.rawClass) as Class<T>

        return this
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T {
        val instance = clazz.getDeclaredConstructor().newInstance()
            ?: throw RuntimeException("No class found")

        val idField = resolveIdField(instance::class)

        if (idField is KMutableProperty<*>) {
            val returnType = idField.returnType.classifier as KClass<*>

            idField.setter.call(instance, p.readValueAs(returnType.java))
        }

        return instance
    }
}
