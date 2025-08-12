package com.since.noteShield.presentance.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalErrorHandling {


    @ExceptionHandler(  MethodArgumentNotValidException::class)
    fun methodError(error: MethodArgumentNotValidException) : ResponseEntity<Map<String,Any>>{

        val validate = error.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?:"Invalid")
        }

        val map = mapOf(
            "Error" to validate
        )

        return ResponseEntity.badRequest().body(map)

    }

}