package com.thealvistar.crudgenerics.serializers

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal data class Foo(
    var id: Int = 0,

    @JsonSerialize(using = IdFlatSerializer::class)
    @JsonDeserialize(using = IdFlatDeserializer::class)
    var bar: Bar? = null,

    @JsonSerialize(contentUsing = IdFlatSerializer::class)
    @JsonDeserialize(contentUsing = IdFlatDeserializer::class)
    var bars: List<Bar>? = null
)

internal data class AnnotatedFoo(
    var id: Int = 0,

    @IdFlatSerDer
    var bar: Bar? = null,

    @IdFlatSerDerCollection
    var bars: List<Bar>? = null
)

internal data class Bar(
    var id: Int = 0,
    var name: String? = null
)

class FlatSerializerTest {
    @Test
    fun `test serialization`() {
        val foo = Foo(
            id = 1,
            bar = Bar(id = 2, name = "bar"),
            bars = listOf(Bar(id = 3, name = "bar1"), Bar(id = 4, name = "bar2"))
        )

        val json = jacksonObjectMapper().writeValueAsString(foo)

        json.shouldContainJsonKeyValue("$.bar", 2)

        json.shouldContainJsonKeyValue("$.bars[0]", 3)
    }

    @Test
    fun `test deserialization`() {
        val json = """
            {
                "id": 1,
                "bar": 2,
                "bars": [3, 4]
            }
        """.trimIndent()

        val foo = jacksonObjectMapper().readValue(json, Foo::class.java)

        foo.id shouldBe 1
        foo.bar?.id shouldBe 2
        foo?.bars?.get(0)?.id shouldBe 3
        foo?.bars?.get(1)?.id shouldBe 4
    }

    @Test
    fun `test annotated serialization`() {
        val foo = AnnotatedFoo(
            id = 1,
            bar = Bar(id = 2, name = "bar"),
            bars = listOf(Bar(id = 3, name = "bar1"), Bar(id = 4, name = "bar2"))
        )

        val json = jacksonObjectMapper().writeValueAsString(foo)

        json.shouldContainJsonKeyValue("$.bar", 2)

        json.shouldContainJsonKeyValue("$.bars[0]", 3)
    }
}
