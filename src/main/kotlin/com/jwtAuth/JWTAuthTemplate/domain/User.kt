package com.jwtAuth.JWTAuthTemplate.domain

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.*

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
data class User(
                @Id
                @GeneratedValue(strategy = GenerationType.AUTO)
                private val id: Long,
                private val name: String,
                private val username: String,
                private val password: String,
                @ManyToMany(fetch = FetchType.EAGER)
                private val roles: Collection<Role> = ArrayList())