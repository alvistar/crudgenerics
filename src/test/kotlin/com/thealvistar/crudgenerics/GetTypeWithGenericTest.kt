package com.thealvistar.crudgenerics

import com.thealvistar.crudgenerics.config.GenericFoo
import com.thealvistar.crudgenerics.config.getTypeWithGenerics
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GenericFoo1 : GenericFoo<String, Int>("a")
class GenericFoo2 : GenericFoo<Int, String>("b")
class GenericFoo3 : GenericFoo<String, String>("c")
class GetTypeWithGenericTest {
    @Test
    fun `get specific class for type in a list`() {
        val list = listOf(
            GenericFoo1(),
            GenericFoo2(),
            GenericFoo3()
        )

        list.getTypeWithGenerics(GenericFoo::class.java, String::class.java, Int::class.java).apply {
            size shouldBe 1
            this[0].name shouldBe "a"
        }

        list.getTypeWithGenerics(GenericFoo::class.java, Int::class.java, String::class.java).apply {
            size shouldBe 1
            this[0].name shouldBe "b"
        }

        list.getTypeWithGenerics(GenericFoo::class.java, String::class.java, String::class.java)
            .apply {
                size shouldBe 1
                this[0].name shouldBe "c"
            }
    }
}
