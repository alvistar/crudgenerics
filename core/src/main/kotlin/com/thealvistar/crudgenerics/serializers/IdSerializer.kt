package com.thealvistar.crudgenerics.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import jakarta.persistence.Id
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

fun getIdField(clazz: KClass<*>) = clazz.memberProperties.find {
    it.javaField?.isAnnotationPresent(Id::class.java) ?: false
}

private fun resolveIdField(clazz: KClass<*>): KProperty<*> {
    val field = getIdField(clazz) ?: clazz.memberProperties.find { it.name == "id" }

    return field ?: throw IllegalArgumentException("Unable to resolve id field for class $clazz")
}

/**
 * Serialize an object to a JSON object with only the id field.
 */

class IdSerializer<T : Any>(clazz: Class<T>? = null) : StdSerializer<T>(clazz) {
    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val idField = resolveIdField(value::class)

        gen.writeStartObject()
        gen.writeStringField("id", idField.call(value).toString())
        gen.writeEndObject()
    }
}

/**
 * Serialize an object to a JSON string representing the id field.
 */

class IdFlatSerializer<T : Any>(clazz: Class<T>? = null) : StdSerializer<T>(clazz) {
    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val idField = resolveIdField(value::class)

        gen.writeString(idField.call(value).toString())
    }
}

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
