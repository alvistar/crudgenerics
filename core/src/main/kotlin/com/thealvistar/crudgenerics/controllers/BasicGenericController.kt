package com.thealvistar.crudgenerics.controllers

abstract class BasicGenericController<T : Any, ID : Any> : DtoGenericController<T, ID, T>()
