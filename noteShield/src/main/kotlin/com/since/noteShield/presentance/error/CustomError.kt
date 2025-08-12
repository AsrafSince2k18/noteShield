package com.since.noteShield.presentance.error

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


class CustomError(errorMessage:String,
    httpStatus: HttpStatus) : ResponseStatusException(httpStatus,errorMessage)


