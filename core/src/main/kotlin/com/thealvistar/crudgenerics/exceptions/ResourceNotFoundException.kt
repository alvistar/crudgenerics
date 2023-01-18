package com.thealvistar.crudgenerics.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource Not Found")
class ResourceNotFoundException(msg: String = "") : RuntimeException(msg)
