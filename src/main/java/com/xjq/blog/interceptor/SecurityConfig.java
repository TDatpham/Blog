
package com.xjq.blog.interceptor;

import com.xjq.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public org.springframework.security.web.SecurityFilterChain securityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/about", "/tags/**", "/types/**", "/archives", "/register", "/css/**", "/js/**",
                        "/images/**", "/admin/login", "/admin", "/blog/**", "/chat/**", "/search")
                .permitAll()
                .antMatchers("/admin/types/**").hasAuthority("ADMIN")
                .antMatchers("/admin/tags/**").hasAuthority("ADMIN")
                .antMatchers("/admin/**").hasAnyAuthority("ADMIN", "USER")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    String username = authentication.getName();
                    com.xjq.blog.model.User user = userService.findByUsername(username);
                    if (user != null) {
                        user.setPassword(null);
                        request.getSession().setAttribute("user", user);
                    }
                    response.sendRedirect("/admin/blogs");
                })
                .failureUrl("/admin/login?error")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin")
                .permitAll()
                .and()
                .csrf().disable();
        return http.build();
    }

    @Autowired
    public void configureGlobal(
            org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(username -> {
            com.xjq.blog.model.User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.isEnabled(),
                    true, true, true,
                    org.springframework.security.core.authority.AuthorityUtils
                            .commaSeparatedStringToAuthorityList(user.getRole()));
        }).passwordEncoder(passwordEncoder);
    }
}
