package com.thealvistar.crudgenerics.utils

import java.util.Locale

val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
fun String.camelToKebabCase(): String {
    return camelRegex.replace(this) {
        "-${it.value}"
    }.lowercase()
}

fun Any.getTag(): String {
    return this::class.simpleName!!.replace("([A-Z0-9])".toRegex(), "-$1")
        .lowercase(Locale.getDefault())
        .removePrefix("-")
}
