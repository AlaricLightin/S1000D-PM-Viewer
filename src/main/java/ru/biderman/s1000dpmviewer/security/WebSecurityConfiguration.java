package ru.biderman.s1000dpmviewer.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()// TODO потом прикрутить обратно
                .authorizeRequests()
                    .antMatchers(HttpMethod.DELETE, "/publication/*")
                    .hasRole(Authorities.ADMIN.toString())
                .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/publication")
                    .hasRole(Authorities.EDITOR.toString())
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

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        JdbcDaoImpl userDetailsService = new JdbcDaoImpl();
        userDetailsService.setDataSource(dataSource);
        return userDetailsService;
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
