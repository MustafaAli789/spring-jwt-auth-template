package com.jwtAuth.JWTAuthTemplate.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.Exception
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthorizationFilter : OncePerRequestFilter() {
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
                    val roles = decodedjwt.getClaim("roles").asArray(String.javaClass)
                    val authorities = ArrayList<SimpleGrantedAuthority>()
                    roles.forEach { role ->
                        authorities.add(SimpleGrantedAuthority(role as String))
                    }
                    val authToken = UsernamePasswordAuthenticationToken(userName, null, authorities)
                    SecurityContextHolder.getContext().authentication = authToken
                    fc.doFilter(req, res)
                } catch (e: Exception) {

                }
            } else {
                fc.doFilter(req, res)
            }
        }
    }
}