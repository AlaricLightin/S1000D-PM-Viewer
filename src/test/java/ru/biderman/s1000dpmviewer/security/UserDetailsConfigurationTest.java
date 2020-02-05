package ru.biderman.s1000dpmviewer.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Сервис по работе с аутентификационными данными пользователей ")
class UserDetailsConfigurationTest {
    @Autowired
    UserDetailsService userDetailsService;

    @DisplayName("должен возвращать роли администратора")
    @Test
    void shouldGetAdminAuthorities() {
        final String adminUsername = "admin";

        UserDetails adminDetails = userDetailsService.loadUserByUsername(adminUsername);
        assertThat(adminDetails)
                .isNotNull();

        assertThat(Authorities.getAuthorities(adminDetails.getAuthorities()))
                .containsExactlyInAnyOrder(Authorities.EDITOR, Authorities.ADMIN);
    }

}