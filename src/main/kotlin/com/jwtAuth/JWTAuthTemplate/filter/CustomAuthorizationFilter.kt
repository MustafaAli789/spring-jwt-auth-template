package com.jwtAuth.JWTAuthTemplate.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class CustomAuthorizationFilter : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, fc: FilterChain) {
        if (req.servletPath == "/api/login") {
            fc.doFilter(req, res) //just pass on, dont do anything here
        } else {
            val authHeader = req.getHeader(AUTHORIZATION)
            if (authHeader !== null && authHeader.startsWith("Bearer ")) {
                try {
                    val token = authHeader.substring("Bearer ".length)
                    val algorithm = Algorithm.HMAC256("YOUR_SECRET_HERE")
                    val verifier = JWT.require(algorithm).build()
                    val decodedjwt = verifier.verify(token)
                    val userName = decodedjwt.subject
                    val roles = ObjectMapper().readValue<Array<String>>(decodedjwt.getClaim("roles").toString())
                    val authorities = ArrayList<SimpleGrantedAuthority>()
                    roles.forEach { role ->
                        authorities.add(SimpleGrantedAuthority(role as String))
                    }
                    val authToken = UsernamePasswordAuthenticationToken(userName, null, authorities)
                    SecurityContextHolder.getContext().authentication = authToken
                    fc.doFilter(req, res)
                } catch (e: Exception) {
                    logger.error("Error logging in: " + e.message)
                    res.setHeader("error", e.message)
                    res.status = 403
                    //res.sendError(403)

                    var errors = mapOf("error_msg" to e.message)
                    res.contentType = MediaType.APPLICATION_JSON_VALUE
                    ObjectMapper().writeValue(res.outputStream, errors)
                }
            } else {
                fc.doFilter(req, res)
            }
        }
    }
}