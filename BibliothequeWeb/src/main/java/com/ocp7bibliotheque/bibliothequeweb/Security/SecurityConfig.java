package com.ocp7bibliotheque.bibliothequeweb.Security;

import com.ocp7bibliotheque.bibliothequeweb.Services.UserServiceDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserServiceDetails userServiceDetails;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.formLogin().loginPage("/login").defaultSuccessUrl("/home", true);
        http.authorizeRequests().antMatchers("/inscription","/register","/login").permitAll();
        http.authorizeRequests().antMatchers("/home","/books").hasAuthority("USER");
        http.authorizeRequests().antMatchers("/administration").hasAuthority("ADMIN");
        http.exceptionHandling().accessDeniedPage("/403");
        http.logout();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceDetails).passwordEncoder(bCryptPasswordEncoder());

    }


}

