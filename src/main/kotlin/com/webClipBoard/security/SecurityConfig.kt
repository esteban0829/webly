package com.webClipBoard.security

import com.webClipBoard.service.AccountService
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@EnableWebSecurity
class SecurityConfig(
    private val accountService: AccountService,
): WebSecurityConfigurerAdapter() {

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**")
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers("/login", "/signup", "/user").permitAll()
            .antMatchers("/").hasRole("USER") // USER, ADMIN can access
            .antMatchers("/admin").hasRole("ADMIN") // only ADMIN can access
            .anyRequest().authenticated() // any request excluding above should have any authentication
            .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureForwardUrl("/hello")
            .and()
            .logout()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(accountService)
            .passwordEncoder(BCryptPasswordEncoder())
    }
}