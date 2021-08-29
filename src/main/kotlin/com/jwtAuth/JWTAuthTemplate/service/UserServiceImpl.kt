package com.jwtAuth.JWTAuthTemplate.service

import com.jwtAuth.JWTAuthTemplate.domain.Role
import com.jwtAuth.JWTAuthTemplate.domain.User
import com.jwtAuth.JWTAuthTemplate.repository.RoleRepo
import com.jwtAuth.JWTAuthTemplate.repository.UserRepo
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@Slf4j //logging
class UserServiceImpl(val userRepo: UserRepo, val roleRepo: RoleRepo): UserService {
    override fun saveUser(user: User): User {
        return userRepo.save(user)
    }

    override fun saveRole(role: Role): Role {
        return roleRepo.save(role)
    }

    override fun addRoleToUser(username: String, roleName: String) {
        val user = userRepo.finderByUsername(username)
        val role = roleRepo.findByName(roleName)
        user.roles.add(role)
    }

    override fun getUser(username: String): User {
        return userRepo.finderByUsername(username)
    }

    override fun getUsers(): List<User> {
        return userRepo.findAll()
    }
}