package com.rakesh.Contact.Manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private UserDetailsService userDetailsService;
    private OAuthAuthenticationSuccessHandler handler;
    private AuthFailureHandler authFailureHandler;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, OAuthAuthenticationSuccessHandler handler, AuthFailureHandler authFailureHandler) {
        this.userDetailsService = userDetailsService;
        this.handler = handler;
        this.authFailureHandler = authFailureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);

        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.csrf(csrf->csrf.disable());
        http.authorizeHttpRequests(request->request.requestMatchers("/user/**").authenticated()
                                                   .anyRequest().permitAll());
        http.formLogin(form->form.loginPage("/login")
                                      .loginProcessingUrl("/authenticate") 
                                      .defaultSuccessUrl("/user/profile")
                                      .failureUrl("/login?error=true")
                                      .usernameParameter("email")
                                      .passwordParameter("password")
                                      .failureHandler(authFailureHandler));

        http.logout(logout->logout.logoutUrl("/logout")
                                        .logoutSuccessUrl("/login?logout=true")
                                        .invalidateHttpSession(true)
                                        .deleteCookies("JSESSIONID"));
        
        // OAuth2 Login Configuration
        http.oauth2Login(oauth->oauth.loginPage("/login")
                                          .successHandler(handler));


        return http.build();
    }
}
