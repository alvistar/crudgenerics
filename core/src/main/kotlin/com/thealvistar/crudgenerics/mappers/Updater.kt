package com.thealvistar.crudgenerics.mappers

import org.mapstruct.MappingTarget

interface Updater<S, T> {
    fun update(source: S, @MappingTarget target: T)
}
