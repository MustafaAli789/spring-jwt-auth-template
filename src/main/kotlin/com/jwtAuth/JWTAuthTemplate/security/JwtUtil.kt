package com.jwtAuth.JWTAuthTemplate.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtUtil {

    //Should preferably not be hardcoded here
    private val algorithm = Algorithm.HMAC256("YOUR_SECRET_HERE")

    //10 min default
    private val JWT_LIFESPAN = 10*60*1000

    fun generateAccessToken(username: String, authorities: List<String>, issuer: String): String {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(Date(System.currentTimeMillis() + JWT_LIFESPAN))
                .withIssuer(issuer)
                .withIssuedAt(Date(System.currentTimeMillis()))
                .withClaim("roles", authorities)
                .sign(algorithm)
    }

    fun generateRereshToken(username: String, issuer: String): String {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(Date(System.currentTimeMillis() + JWT_LIFESPAN * 10))
                .withIssuer(issuer)
                .withIssuedAt(Date(System.currentTimeMillis()))
                .sign(algorithm)
    }

    fun verifyTokenInAuthHeader(authHeader: String): DecodedJWT {
        val token = authHeader.substring("Bearer ".length)
        val verifier = JWT.require(algorithm).build()
        return verifier.verify(token)
    }

}