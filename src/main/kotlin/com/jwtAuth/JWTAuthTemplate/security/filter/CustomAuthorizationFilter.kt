package com.jwtAuth.JWTAuthTemplate.security.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jwtAuth.JWTAuthTemplate.security.JwtUtil
import com.jwtAuth.JWTAuthTemplate.utils.ResponseUtil
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class CustomAuthorizationFilter(private val jwtUtil: JwtUtil) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, fc: FilterChain) {
        if (req.servletPath == "/api/login" || req.servletPath.startsWith("/api/auth")) {
            fc.doFilter(req, res)
        } else {
            val authHeader = req.getHeader(AUTHORIZATION)
            if (authHeader !== null && authHeader.startsWith("Bearer ")) {
                try {
                    val decodedJWT = jwtUtil.verifyTokenInAuthHeader(authHeader)

                    val userName = decodedJWT.subject
                    val roles = ObjectMapper().readValue<Array<String>>(decodedJWT.getClaim("roles").toString())
                    val authorities = ArrayList<SimpleGrantedAuthority>()
                    roles.forEach { role ->
                        authorities.add(SimpleGrantedAuthority(role))
                    }
                    val authToken = UsernamePasswordAuthenticationToken(userName, null, authorities)

                    SecurityContextHolder.getContext().authentication = authToken
                    fc.doFilter(req, res)
                } catch (e: Exception) {
                    logger.error("Error logging in: " + e.message)
                    ResponseUtil.sendResponseErr(e.message, 403, res)
                }
            } else {
                fc.doFilter(req, res)
            }
        }
    }
}