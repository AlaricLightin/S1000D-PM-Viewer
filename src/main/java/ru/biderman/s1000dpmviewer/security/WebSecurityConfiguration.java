package ru.biderman.s1000dpmviewer.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import ru.biderman.s1000dpmviewer.domain.UserRole;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JdbcUserDetailsManager jdbcUserDetailsManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()// TODO потом прикрутить обратно
                .authorizeRequests()
                    .antMatchers("/user", "/user/**")
                    .hasRole(UserRole.ADMIN.toString())
                    .antMatchers(HttpMethod.DELETE, "/publication/*")
                    .hasRole(UserRole.ADMIN.toString())
                    .antMatchers("/publication/*/authorizations")
                    .hasRole(UserRole.ADMIN.toString())
                    .antMatchers("/actuator/logfile")
                    .hasRole(UserRole.ADMIN.toString())
                .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/publication")
                    .hasRole(UserRole.EDITOR.toString())
                .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET,
                            "/",
                            "/publication", "/publication/**",
                            "/login")
                    .permitAll()
                .and()
                .httpBasic()
                    .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jdbcUserDetailsManager);
    }

    @Bean
    public BasicAuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint authenticationEntryPoint = new BasicAuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request,
                                 HttpServletResponse response,
                                 AuthenticationException authException) throws IOException {
                response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized Access!!" );
            }
        };
        authenticationEntryPoint.setRealmName("S1000D PM Viewer");
        return authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
