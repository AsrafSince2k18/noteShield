package com.since.noteShield.presentance.controller

import com.since.noteShield.data.database.modal.User
import com.since.noteShield.data.security.AuthService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {

    data class TokenRequest(
        @field:NotBlank(message = "Must enter the token")
        val refreshToken: String
    )

    data class AuthRequest(
        @field:Email(
            message = "Please enter valid email address",
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )
        val email: String,
        @field:Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Please enter valid password"
        )
        val password: String
    )


    @PostMapping("/signup")
    fun signUp(
        @RequestBody @Valid body : AuthRequest
    ): ResponseEntity<User>{
        val user = authService.signUp(
                email = body.email,
                password = body.password
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(user)

    }


    @PostMapping("/signin")
    fun signIn(
        @RequestBody @Valid body : AuthRequest
    ): ResponseEntity<AuthService.TokenPair>{
        val user = authService.signIn(
            email = body.email,
            password = body.password
        )
        return ResponseEntity.status(HttpStatus.OK).body(user)
    }


    @PostMapping("/refresh")
    fun refresh(
        @RequestBody @Valid body : TokenRequest
    ): ResponseEntity<AuthService.TokenPair>{
        val user = authService.refreshToken(
           token = body.refreshToken
        )
        return ResponseEntity.status(HttpStatus.OK).body(user)
    }



}