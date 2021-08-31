package com.jwtAuth.JWTAuthTemplate.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class CustomAuthenticationFilter(private val authManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    //Will be running whenever someone is signing in
    //normally hooked up to /login
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val username = request.getParameter("username")
        val password = request.getParameter("password")
        logger.info("Username is: $username")
        logger.info("Password is: $password")
        val authToken = UsernamePasswordAuthenticationToken(username, password)
        return authManager.authenticate(authToken)
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication) {
        val secUser = authResult.principal as User
        val algorithm = Algorithm.HMAC256("YOUR_SECRET_HERE") //signing token with this
        val accessToken = JWT.create()
                .withSubject(secUser.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 10*1000))
                .withIssuer(request.requestURL.toString())
                .withClaim("roles", secUser.authorities.map { authority -> authority.authority })
                .sign(algorithm)
        val refreshToken = JWT.create()
                .withSubject(secUser.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 30*60*1000))
                .withIssuer(request.requestURL.toString())
                .sign(algorithm)
        var tokens = mapOf("access_token" to accessToken, "refresh_token" to refreshToken)
        response.contentType = APPLICATION_JSON_VALUE
        ObjectMapper().writeValue(response.outputStream, tokens)
    }
}