package com.thealvistar.crudgenerics.serializers

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
)
@Retention(
    AnnotationRetention.RUNTIME,
)
@JacksonAnnotationsInside
@JsonSerialize(using = IdFlatSerializer::class)
@JsonDeserialize(using = IdFlatDeserializer::class)
annotation class IdFlatSerDer
