package com.thealvistar.crudgenerics.controllers

abstract class VerySimpleGenericController<T : Any, ID : Any> : SimpleGenericController<T, T, ID>()
