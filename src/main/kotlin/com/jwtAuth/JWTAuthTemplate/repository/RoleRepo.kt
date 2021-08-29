package com.jwtAuth.JWTAuthTemplate.repository

import com.jwtAuth.JWTAuthTemplate.domain.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepo: JpaRepository<Role, Long> {
    fun findByName(name: String): Role
}