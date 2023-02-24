package com.thealvistar.crudgenerics.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(
    value = HttpStatus.FORBIDDEN,
    reason = "Forbidden access to resource",
)
class ForbiddenException(msg: String = "") : RuntimeException(msg)
