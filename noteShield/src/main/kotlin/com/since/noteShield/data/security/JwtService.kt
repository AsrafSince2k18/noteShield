package com.since.noteShield.data.security

import io.jsonwebtoken.ClaimJwtException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class JwtService(
    @Value("\${JWT_KEY}") private val secretKey: String
) {

    companion object {
        private const val ACCESS_TOKEN_EXPIRY = 15L * 60L * 1000L
        const val REFRESH_TOKEN_EXPIRY = 1L * 60L * 60L * 1000L
    }

    private val key = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(secretKey)
    )


    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long
    ): String {

        val now = Date()
        val expiryTime = Date(now.time + expiry)

        return Jwts
            .builder()
            .header()
            .type("JWT")
            .and()
            .subject(userId)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryTime)
            .signWith(key, Jwts.SIG.HS256)
            .compact()

    }


    fun generateAccessToken(
        userId: String
    ): String {
        return generateToken(userId = userId, type = "access", ACCESS_TOKEN_EXPIRY)
    }


    fun generateRefreshToken(
        userId: String
    ): String {
        return generateToken(userId = userId, type = "refresh", REFRESH_TOKEN_EXPIRY)
    }


    //TODO: THIS METHOD VERIFY YOU SECRET KET AND TOKEN EXPIRY CHECK
    //TODO: DECODE THE TOKEN SEPREATE THE TOKEN FINALY PAYLOAD ONLY RETURN
    private fun pairClaims(
        token: String
    ): Claims? {

        val rawToken = if (token.startsWith("Bearer ")) {
            token.substringAfter("Bearer ")
        } else {
            token
        }
        return try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(rawToken)
                .payload

        } catch (e: ClaimJwtException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
        } catch (e: JwtException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
        }

    }


    fun validateAccessToken(token: String): Boolean {
        val parse = pairClaims(token) ?: return false
        val access = parse["type"] as String
        return access == "access"
    }

    fun validateRefreshToken(token: String): Boolean {
        val parse = pairClaims(token) ?: return false
        val access = parse["type"] as String
        return access == "refresh"
    }

    fun getByUserIdByToken(token: String): String? {
        val parse = pairClaims(token) ?: return null
        return parse.subject
    }

}