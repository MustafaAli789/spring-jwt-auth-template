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
}

data class RoleToUserForm(val username: String, val roleName: String)
