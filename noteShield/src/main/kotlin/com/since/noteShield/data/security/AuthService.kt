package com.since.noteShield.data.security

import com.since.noteShield.data.database.modal.RefreshToken
import com.since.noteShield.data.database.modal.User
import com.since.noteShield.data.database.repository.RefreshTokenRepository
import com.since.noteShield.data.database.repository.UserRepository
import com.since.noteShield.data.security.JwtService.Companion.REFRESH_TOKEN_EXPIRY
import com.since.noteShield.presentance.error.CustomError
import io.jsonwebtoken.JwtException
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val hashedPassword: HashedPassword,
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {


    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    fun signUp(
        email: String,
        password: String
    ) :User{

        val findUser = userRepository.findByEmail(email.trim())
        if (findUser != null) {
            throw CustomError("Already use this email please kind different email address", HttpStatus.CONFLICT)
        }

        val user = User(
            email = email.trim(),
            password = hashedPassword.encodePassword(password)
        )
        return userRepository.save(user)

    }


    fun signIn(
        email: String,
        password: String
    ): TokenPair {


        val findUser = userRepository.findByEmail(email.trim())
        if (findUser == null) {
            throw CustomError("Email id not found", HttpStatus.NOT_FOUND)
        }

        if (!hashedPassword.matchPassword(rawPassword = password, encodePassword = findUser.password)) {
            throw CustomError("Invalid credential", HttpStatus.UNAUTHORIZED)

        }


        val accessToken = jwtService.generateAccessToken(findUser.id.toHexString())
        val refreshToken = jwtService.generateRefreshToken(findUser.id.toHexString())

        storeRefreshToken(token = refreshToken)

        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshToken
        )

    }

    @Transactional
    fun refreshToken(
        token: String
    ): TokenPair {

        if (!jwtService.validateRefreshToken(token)) {
            throw CustomError("Refresh token expired or inValid", HttpStatus.UNAUTHORIZED)
        }

        val userId = jwtService.getByUserIdByToken(token = token)
            ?: throw CustomError("User not found", HttpStatus.UNAUTHORIZED)

        val user = userRepository.findById(ObjectId(userId))
            .orElseThrow {
                throw CustomError("Invalid credential", HttpStatus.UNAUTHORIZED)
            }


        val hasToken = hashedToken(token = token)
        val token=refreshTokenRepository.findByOwnerIdAndHasToken(user.id, hasToken = hasToken)
        if(token!=null){
            val deleteToken = refreshTokenRepository.deleteByOwnerIdAndHasToken(user.id, hasToken = hasToken)
            if(deleteToken==0L){
                throw CustomError("Cannot delete or Token may be used", httpStatus = HttpStatus.UNAUTHORIZED)
            }
        }

        val accessToken = jwtService.generateAccessToken(user.id.toHexString())
        val refreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(refreshToken)
        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshToken
        )

    }


    private fun storeRefreshToken(token: String) {

        try {
            if (jwtService.validateRefreshToken(token)) {
                val hashedToken = hashedToken(token)
                val userId = jwtService.getByUserIdByToken(token = token) ?: throw ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token"
                )

                val user = userRepository.findById(ObjectId(userId))
                    .orElseThrow {
                        ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")
                    }

                val expiry = REFRESH_TOKEN_EXPIRY
                val expiryAt = Instant.now().plusMillis(expiry)

                val refreshToken = RefreshToken(
                    ownerId = user.id,
                    expiryAt = expiryAt,
                    hasToken = hashedToken
                )
                refreshTokenRepository.save(refreshToken)
            }
        }catch (e: JwtException){
            throw e
        }catch (e: Exception){
            throw e
        }
    }

    private fun hashedToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }

}