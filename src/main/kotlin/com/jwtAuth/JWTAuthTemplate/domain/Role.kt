package com.jwtAuth.JWTAuthTemplate.domain

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
data class Role(
                @Id
                @GeneratedValue(strategy = GenerationType.AUTO)
                private val id: Long,
                private val name: String)