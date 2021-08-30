package com.jwtAuth.JWTAuthTemplate

import com.jwtAuth.JWTAuthTemplate.domain.Role
import com.jwtAuth.JWTAuthTemplate.domain.User
import com.jwtAuth.JWTAuthTemplate.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class JwtAuthTemplateApplication(val userService: UserService) : CommandLineRunner {
	override fun run(vararg args: String?) {
		userService.saveRole(Role(null, "ROLE_USER"))
		userService.saveRole(Role(null, "ROLE_MANAGER"))
		userService.saveRole(Role(null, "ROLE_ADMIN"))
		userService.saveRole(Role(null, "ROLE_SUPER_ADMIN"))

		userService.saveUser(User(null, "Johnny Bravo", "john", "1234", ArrayList<Role>()))
		userService.saveUser(User(null, "Will Smith", "will", "1234", ArrayList<Role>()))
		userService.saveUser(User(null, "Leo Dicaprio", "leo", "1234", ArrayList<Role>()))
		userService.saveUser(User(null, "Arnold schwarnegger", "arnold", "1234", ArrayList<Role>()))

		userService.addRoleToUser("john", "ROLE_USER")
		userService.addRoleToUser("john", "ROLE_MANAGER")
		userService.addRoleToUser("will", "ROLE_MANAGER")
		userService.addRoleToUser("leo", "ROLE_ADMIN")
		userService.addRoleToUser("arnold", "ROLE_SUPER_ADMIN")
		userService.addRoleToUser("arnold", "ROLE_ADMIN")
		userService.addRoleToUser("arnold", "ROLE_USER")
	}

}

fun main(args: Array<String>) {
	runApplication<JwtAuthTemplateApplication>(*args)
}



