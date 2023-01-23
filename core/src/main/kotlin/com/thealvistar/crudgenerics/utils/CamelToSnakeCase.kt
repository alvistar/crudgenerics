package com.thealvistar.crudgenerics.utils

val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
fun String.camelToKebabCase(): String {
    return camelRegex.replace(this) {
        "-${it.value}"
    }.lowercase()
}
