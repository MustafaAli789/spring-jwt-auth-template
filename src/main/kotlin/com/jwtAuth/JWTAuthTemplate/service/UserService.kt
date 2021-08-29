package com.jwtAuth.JWTAuthTemplate.service

import com.jwtAuth.JWTAuthTemplate.domain.Role
import com.jwtAuth.JWTAuthTemplate.domain.User

interface UserService {
    fun saveUser(user: User): User
    fun saveRole(role: Role): Role
    fun addRoleToUser(username: String, roleName: String)
    fun getUser(username: String): User
    fun getUsers(): List<User>
}