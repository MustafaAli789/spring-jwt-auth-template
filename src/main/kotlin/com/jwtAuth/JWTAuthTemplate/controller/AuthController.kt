package com.jwtAuth.JWTAuthTemplate.controller

import com.jwtAuth.JWTAuthTemplate.security.JwtUtil
import com.jwtAuth.JWTAuthTemplate.service.UserService
import com.jwtAuth.JWTAuthTemplate.utils.ResponseUtil
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
@RestController
@RequestMapping("/api/auth")
class AuthController(val authManager: AuthenticationManager,
                     val userService: UserService,
                     val jwtUtil: JwtUtil) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/login")
    fun login(@RequestBody loginData: AuthenticationRequest, req: HttpServletRequest, res: HttpServletResponse) {
        try {
            authManager.authenticate(
                    UsernamePasswordAuthenticationToken(loginData.username, loginData.password)
            )
        } catch (e: BadCredentialsException) {
            throw Exception("Incorrect username or password", e)
        }
        val user = userService.getUser(loginData.username)
        val accessToken = jwtUtil.generateAccessToken(user.username, user.roles.map{ role -> role.name }, req.servletPath)
        val refreshToken = jwtUtil.generateRereshToken(user.username, req.servletPath)
        ResponseUtil.sendMapResponse(mapOf("access_token" to accessToken, "refresh_token" to refreshToken), res)
    }

    @GetMapping("/refresh")
    fun refreshToken(req: HttpServletRequest, res: HttpServletResponse) {
        val authHeader = req.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader !== null && authHeader.startsWith("Bearer ")) {
            try {
                val decodedJWT = jwtUtil.verifyTokenInAuthHeader(authHeader)

                val user = userService.getUser(decodedJWT.subject)
                val accessToken = jwtUtil.generateAccessToken(user.username, user.roles.map{ role -> role.name }, req.servletPath)
                ResponseUtil.sendMapResponse(mapOf("access_token" to accessToken), res)
            } catch (e: Exception) {
                logger.error("Error logging in: " + e.message)
                ResponseUtil.sendResponseErr(e.message, 403, res)
            }
        } else {
            throw RuntimeException("Refresh token is missing")
        }
    }
}

data class AuthenticationRequest(val username: String, val password: String)