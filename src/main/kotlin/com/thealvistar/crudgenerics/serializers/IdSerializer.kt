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
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class IdSerializer<T : Any>(clazz: Class<T>? = null) : StdSerializer<T>(clazz) {
    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val idField = value::class.memberProperties.find {
            it.javaField?.isAnnotationPresent(Id::class.java) ?: false
        } ?: throw RuntimeException("No @Id field found in ${value::class}")

        gen.writeStartObject()
        gen.writeStringField("id", idField.call(value).toString())
        gen.writeEndObject()
    }
}

class IdSerializerFlat<T : Any>(clazz: Class<T>? = null) : StdSerializer<T>(clazz) {
    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val idField = value::class.memberProperties.find {
            it.javaField?.isAnnotationPresent(Id::class.java) ?: false
        } ?: throw RuntimeException("No @Id field found in ${value::class}")

        gen.writeString(idField.call(value).toString())
    }
}

class IdDeserializer<T : Any>(clazz: Class<T>? = null, property: BeanProperty? = null) :
    JsonDeserializer<T>(), ContextualDeserializer {

    private lateinit var clazz: Class<T>

    override fun createContextual(
        ctxt: DeserializationContext,
        property: BeanProperty
    ): JsonDeserializer<*> {
        clazz = property.type.contentType.rawClass as Class<T>
        return this
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T {
        val instance = clazz.getDeclaredConstructor().newInstance()
            ?: throw RuntimeException("No class found")

        val idField = clazz.declaredFields.find {
            it.isAnnotationPresent(Id::class.java)
        } ?: throw RuntimeException("No @Id field found in $clazz")

        idField.apply {
            isAccessible = true
            set(instance, p.readValueAs(this.type))
        }

        return instance
    }
}
