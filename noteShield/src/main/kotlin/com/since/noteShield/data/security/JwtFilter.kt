package com.since.noteShield.data.security

import com.since.noteShield.data.database.repository.UserRepository
import com.since.noteShield.presentance.error.CustomError
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bson.types.ObjectId
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : OncePerRequestFilter(){



    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {


        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        if(header==null || !header.startsWith("Bearer ")){
            filterChain.doFilter(request,response)
            return
        }

        val token = header.substringAfter("Bearer ")
        try {
            if (jwtService.validateAccessToken(token)){
                val userId = jwtService.getByUserIdByToken(token)
                val user = userRepository.findById(ObjectId(userId))
                    .orElseThrow {
                        CustomError("Invalidate token", HttpStatus.UNAUTHORIZED)
                    }

                val securityContext = UsernamePasswordAuthenticationToken(user,null,emptyList())

                securityContext.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication=securityContext
            }

        }catch (e: JwtException){
            throw e
        }catch (e: MalformedJwtException){
            throw e
        }catch (e: Exception){
            throw e
        }

        filterChain.doFilter(request,response)

    }
}