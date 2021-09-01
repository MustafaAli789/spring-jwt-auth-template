package com.jwtAuth.JWTAuthTemplate.security

import com.jwtAuth.JWTAuthTemplate.security.filter.CustomAuthorizationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(val userDetailsService: UserDetailsService,
                     val jwtUtil: JwtUtil,
                     val bcryptPassEncoder: PasswordEncoder) : WebSecurityConfigurerAdapter() {

    //This method im assuming is impl first so as to setup the auth manager and then the second configures incoming http reqs
    // We implemented user details service interface in our own user service impl so this is gonna use that bean
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(bcryptPassEncoder)
    }

    // Any request to /api/auth/** is permitted without authenticated user but rest need to be authenticated
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(STATELESS)
        http.authorizeRequests().antMatchers("/api/auth/**").permitAll()
        //http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/user/**").hasAnyAuthority("ROLE_USER")
        //http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/user/save/**").hasAnyAuthority("ROLE_ADMIN")
        http.authorizeRequests().anyRequest().authenticated()
        http.addFilterBefore(CustomAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}