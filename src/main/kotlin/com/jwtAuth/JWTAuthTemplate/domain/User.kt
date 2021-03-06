package com.jwtAuth.JWTAuthTemplate.domain

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long?,
        val name: String,
        val username: String,
        var password: String,
        @ManyToMany(fetch = FetchType.EAGER)
        val roles: MutableCollection<Role> = ArrayList())