package com.jwtAuth.JWTAuthTemplate.service

import com.jwtAuth.JWTAuthTemplate.domain.Role
import com.jwtAuth.JWTAuthTemplate.domain.User
import com.jwtAuth.JWTAuthTemplate.repository.RoleRepo
import com.jwtAuth.JWTAuthTemplate.repository.UserRepo
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j //logging
class UserServiceImpl(val userRepo: UserRepo,
                      val roleRepo: RoleRepo,
                      private val passEncoder: BCryptPasswordEncoder): UserService, UserDetailsService {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepo.findByUsername(username)
        if (user == null) {
            logger.error("User not found in the database")
            throw UsernameNotFoundException("User not found in database")
        } else {
            logger.info("User found in the database: {}", username)
        }
        val authorities: MutableList<SimpleGrantedAuthority>  = LinkedList()
        user.roles.forEach { role ->
            authorities.add(SimpleGrantedAuthority(role.name))
        }
        return org.springframework.security.core.userdetails.User(user.username, user.password, authorities)
    }

    override fun saveUser(user: User): User {
        logger.info("Saving new user {} to db", user.name)
        user.password = passEncoder.encode(user.password).toString()
        return userRepo.save(user)
    }

    override fun saveRole(role: Role): Role {
        logger.info("Saving new role {} to db", role.name)
        return roleRepo.save(role)
    }

    override fun addRoleToUser(username: String, roleName: String) {
        logger.info("Adding role {} to user {}", roleName, username)
        val user = userRepo.findByUsername(username)
        val role = roleRepo.findByName(roleName)
        user.roles.add(role)
    }

    override fun getUser(username: String): User {
        logger.info("Fetching user {}", username)
        return userRepo.findByUsername(username)
    }

    override fun getUsers(): List<User> {
        logger.info("Fetching all users")
        return userRepo.findAll()
    }
}