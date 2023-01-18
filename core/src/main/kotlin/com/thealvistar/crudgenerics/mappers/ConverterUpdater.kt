package com.thealvistar.crudgenerics.mappers

import org.springframework.core.convert.converter.Converter

interface ConverterUpdater<S, T> : Updater<S, T>, Converter<S, T>
