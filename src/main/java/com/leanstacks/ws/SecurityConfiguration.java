package com.leanstacks.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.leanstacks.ws.security.AccountAuthenticationProvider;

/**
 * The SecurityConfiguration class provides a centralized location for
 * application security configuration. This class bootstraps the Spring Security
 * components during application startup.
 * 
 * @author Matt Warman
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private AccountAuthenticationProvider accountAuthenticationProvider;

    /**
     * Supplies a PasswordEncoder instance to the Spring ApplicationContext. The
     * PasswordEncoder is used by the AuthenticationProvider to perform one-way
     * hash operations on passwords for credential comparison.
     * 
     * @return A PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This method builds the AuthenticationProvider used by the system to
     * process authentication requests.
     * 
     * @param auth An AuthenticationManagerBuilder instance used to construct
     *        the AuthenticationProvider.
     * @throws Exception Thrown if a problem occurs constructing the
     *         AuthenticationProvider.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {

        auth.authenticationProvider(accountAuthenticationProvider);

    }
    
    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurerAdapter extends
            WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            
            http
            .csrf().disable()
            .antMatcher("/api/**")
              .authorizeRequests()
                .anyRequest().hasRole("USER")
            .and()
            .httpBasic()
            .and()
            .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
            
        }
        
    }
    
    @Configuration
    @Order(2)
    public static class ActuatorWebSecurityConfigurerAdapter extends
            WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            
            http
            .csrf().disable()
            .antMatcher("/actuators/**")
              .authorizeRequests()
                .anyRequest().hasRole("SYSADMIN")
            .and()
            .httpBasic()
            .and()
            .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
            
        }
        
    }
    
    @Profile("docs")
    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends 
            WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
    
            http
              .csrf().disable()
              .authorizeRequests()
                .anyRequest().authenticated()
              .and()
              .formLogin();
    
        }
    
    }

}
