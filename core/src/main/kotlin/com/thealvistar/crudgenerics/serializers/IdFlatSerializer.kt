package com.thealvistar.crudgenerics.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * Serialize an object to a JSON string representing the id field.
 */

class IdFlatSerializer<T : Any>(clazz: Class<T>? = null) : StdSerializer<T>(clazz) {
    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        val idField = resolveIdField(value::class)

        gen.writeString(idField.call(value).toString())
    }
}
