package com.since.noteShield.data.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component


@Component
class HashedPassword {

    private val bCrypt = BCryptPasswordEncoder()

    fun encodePassword(rawPassword: String):String=bCrypt.encode(rawPassword)

    fun matchPassword(rawPassword: String,encodePassword:String) : Boolean = bCrypt.matches(rawPassword,encodePassword)


}