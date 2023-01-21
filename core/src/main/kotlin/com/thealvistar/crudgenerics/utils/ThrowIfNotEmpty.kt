package com.thealvistar.crudgenerics.utils

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException

fun <T> Set<ConstraintViolation<T>>.throwIfNotEmpty() {
    if (this.isNotEmpty()) {
        throw ConstraintViolationException(this)
    }
}
