package com.thealvistar.crudgenerics.serializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

// Deserialize id only and nullify the rest of the fields
// If the id is not found, the object is nullified
class IdDeserializer<T : Any>(private var clazz: Class<T>? = null) :
    StdDeserializer<T>(clazz),
    ContextualDeserializer {
    override fun createContextual(
        ctxt: DeserializationContext,
        property: BeanProperty?
    ): JsonDeserializer<*> {
        return IdDeserializer(property?.type?.rawClass as Class<T>?)
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T? {
        // Read value as Map<String, Any>
        val type = object : TypeReference<Map<String, Any>>() {}

        val map: Map<String, Any> = p.readValueAs(type)

        val idField = resolveIdField(clazz!!.kotlin)

        val id = map[idField.name]

        // Get instance of current object mapper
        val mapper = p.codec as ObjectMapper

        return if (id != null) {
            // Instantiate new instance of clazz
            val instance =
                clazz?.getDeclaredConstructor()?.newInstance()
                    ?: throw RuntimeException("Cannot instantiate class ${clazz?.name}")

            // Set id field
            if (idField !is KMutableProperty<*>) {
                throw RuntimeException("Id field is not mutable")
            }
            val returnType = idField.returnType.classifier as KClass<*>
            idField.setter.call(instance, mapper.convertValue(id, returnType.java))
            instance
        } else {
            null
        }
    }
}
