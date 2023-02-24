package com.thealvistar.crudgenerics.controllers

/**
 * Basic generic controller using entity for body requests and response.
 * It contains all the basic CRUD operations.
 * @param T The entity class
 * @param ID The ID class
 */
abstract class BasicGenericController<T : Any, ID : Any> : DtoGenericController<T, ID, T>()
