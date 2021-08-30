package com.jwtAuth.JWTAuthTemplate.domain

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.*

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
data class Role(
                @Id
                @GeneratedValue(strategy = GenerationType.AUTO)
                val id: Long,
                val name: String)