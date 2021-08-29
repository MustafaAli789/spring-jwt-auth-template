package com.jwtAuth.JWTAuthTemplate.service

import com.jwtAuth.JWTAuthTemplate.domain.Role
import com.jwtAuth.JWTAuthTemplate.domain.User
import com.jwtAuth.JWTAuthTemplate.repository.RoleRepo
import com.jwtAuth.JWTAuthTemplate.repository.UserRepo
import jdk.nashorn.internal.runtime.regexp.joni.Config.log
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j //logging
class UserServiceImpl(val userRepo: UserRepo, val roleRepo: RoleRepo): UserService {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun saveUser(user: User): User {
        logger.info("Saving new user {} to db", user.name)
        return userRepo.save(user)
    }

    override fun saveRole(role: Role): Role {
        logger.info("Saving new role {} to db", role.name)
        return roleRepo.save(role)
    }

    override fun addRoleToUser(username: String, roleName: String) {
        logger.info("Adding role {} to user {}", roleName, username)
        val user = userRepo.finderByUsername(username)
        val role = roleRepo.findByName(roleName)
        user.roles.add(role)
    }

    override fun getUser(username: String): User {
        logger.info("Fetching user {}", username)
        return userRepo.finderByUsername(username)
    }

    override fun getUsers(): List<User> {
        logger.info("Fetching all users")
        return userRepo.findAll()
    }
}