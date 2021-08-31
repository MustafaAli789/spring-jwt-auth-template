package com.jwtAuth.JWTAuthTemplate.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jwtAuth.JWTAuthTemplate.domain.Role
import com.jwtAuth.JWTAuthTemplate.domain.User
import com.jwtAuth.JWTAuthTemplate.service.UserService
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.lang.Exception
import java.lang.RuntimeException
import java.net.URI
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController()
@RequestMapping("/api")
@RequiredArgsConstructor
class UserController(val userService: UserService) {

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok().body(userService.getUsers())
    }

    @PostMapping("/user/save")
    fun saveUser(@RequestBody user: User): ResponseEntity<User> {
        val uri: URI = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString())
        return ResponseEntity.created(uri).body(userService.saveUser(user))
    }

    @PostMapping("/role/save")
    fun saveRole(@RequestBody role: Role): ResponseEntity<Role> {
        val uri: URI = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString())
        return ResponseEntity.created(uri).body(userService.saveRole(role))
    }

    @PostMapping("/role/addtouser")
    fun addRoleToUser(@RequestBody roleToUserForm: RoleToUserForm): ResponseEntity<Any> {
        userService.addRoleToUser(roleToUserForm.username, roleToUserForm.roleName)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/token/refresh")
    fun refreshToken(req: HttpServletRequest, res: HttpServletResponse) {
        val authHeader = req.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader !== null && authHeader.startsWith("Bearer ")) {
            try {
                val refreshToken = authHeader.substring("Bearer ".length)
                val algorithm = Algorithm.HMAC256("YOUR_SECRET_HERE")
                val verifier = JWT.require(algorithm).build()
                val decodedjwt = verifier.verify(refreshToken)
                val userName = decodedjwt.subject
                val user = userService.getUser(userName)

                val accessToken = JWT.create()
                        .withSubject(user.username)
                        .withExpiresAt(Date(System.currentTimeMillis() + 10*60*1000))
                        .withIssuer(req.requestURL.toString())
                        .withClaim("roles", user.roles.map { role -> role.name })
                        .sign(algorithm)

                var tokens = mapOf("access_token" to accessToken)
                res.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(res.outputStream, tokens)

            } catch (e: Exception) {
                res.setHeader("error", e.message)
                res.status = 403
                //res.sendError(403)

                var errors = mapOf("error_msg" to e.message)
                res.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(res.outputStream, errors)
            }
        } else {
            throw RuntimeException("Refresh token is missing")
        }
    }
}

data class RoleToUserForm(val username: String, val roleName: String)
