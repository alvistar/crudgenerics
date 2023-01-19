package com.thealvistar.crudgenerics.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

interface MyGenericFoo<T, U, G>
class Foo : MyGenericFoo<String, Int, Boolean>

class ResolveGenericKtTest {
    @Test
    fun resolveGeneric() {
        val type = resolveGeneric<String>(Foo(), MyGenericFoo::class, 0)
        assertEquals(String::class.java, type)
    }
}
