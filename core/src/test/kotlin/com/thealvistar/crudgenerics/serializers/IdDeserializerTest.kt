package com.thealvistar.crudgenerics.serializers

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.util.UUID

class IdDeserializerTest {
    data class Foo(
        var id: Int = 0,
        @JsonDeserialize(using = IdDeserializer::class)
        var bar: Bar? = null
    )

    data class Bar(
        var id: Int = 0,
        var name: String? = null
    )

    data class FooUUID(
        var id: UUID? = null,

        @JsonDeserialize(using = IdDeserializer::class)
        var bar: BarUUID? = null
    )

    data class BarUUID(
        var id: UUID? = null,
        var name: String? = null
    )

    @Test
    fun deserialize() {
        @Language("JSON")
        val json = """
            {
                "id": 1,
                "bar": {
                    "id": 2,
                    "name": "bar"
                }
            }
        """.trimIndent()

        jacksonObjectMapper().readValue(json, Foo::class.java)
            .apply {
                id shouldBe 1
                bar?.id shouldBe 2
                bar?.name shouldBe null
            }
    }

    @Test
    fun `deserialize with bar with null id`() {
        @Language("JSON")
        val json = """
            {
                "id": 1,
                "bar": {
                    "name": "bar"
                }
            }
        """.trimIndent()

        jacksonObjectMapper().readValue(json, Foo::class.java)
            .apply {
                id shouldBe 1
                bar shouldBe null
            }
    }

    @Test
    fun `deserialize FooBarUUID`() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        @Language("JSON")
        val json = """
            {
                "id": "$id1",
                "bar": {
                    "id": "$id2",
                    "name": "bar"
                }
            }
        """.trimIndent()

        jacksonObjectMapper().readValue(json, FooUUID::class.java)
            .apply {
                id shouldBe id1
                bar?.id shouldBe id2
                bar?.name shouldBe null
            }
    }
}
