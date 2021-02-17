package com.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests().antMatchers("/login**").permitAll()
                .and()
                .authorizeRequests().antMatchers(("/sign**")).permitAll()
                .and()
                .authorizeRequests().antMatchers("/usersm").hasAnyAuthority("USER","ADMIN")
                .and()
                .authorizeRequests().antMatchers("/foo").hasAnyAuthority("USER","ADMIN") //foo url can be called USER and ADMIN
                // but which one is able to call foo method, we will define via @PreAuthorize("hasRole('Admin')") on foo() method
                .and()
                .authorizeRequests().anyRequest().hasAnyAuthority("ADMIN","USER") //any request except (Admin and User) will not be permitted
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                .and()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/usersm",true)
                    .permitAll()
                .and()
                .logout().logoutSuccessUrl(("/login")).permitAll();
//                .and().csrf().disable(); automaticly csrf will be enable
    }

}
