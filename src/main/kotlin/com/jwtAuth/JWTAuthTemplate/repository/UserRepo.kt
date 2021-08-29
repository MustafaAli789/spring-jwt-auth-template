package com.jwtAuth.JWTAuthTemplate.repository

import com.jwtAuth.JWTAuthTemplate.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo: JpaRepository<User, Long> {
    fun finderByUsername(username: String): User
}