package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.repositories.UserDao;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Сервис пользователей ")
class UserServiceImplTest {
    private UserServiceImpl userService;
    private UserDao userDao;
    private JdbcUserDetailsManager jdbcUserDetailsManager;

    private static final String USERNAME = "username";

    @BeforeEach
    void init() {
        userDao = mock(UserDao.class);
        jdbcUserDetailsManager = mock(JdbcUserDetailsManager.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userDao, jdbcUserDetailsManager, passwordEncoder);
    }

    @DisplayName("должен возвращать всех")
    @Test
    void shouldFindAll() {
        UserData user1 = new UserData("username1", null, Collections.singleton(UserRole.ADMIN));
        UserData user2 = new UserData("username2", null, Collections.emptySet());
        when(userDao.findAllUsers()).thenReturn(Arrays.asList(user1, user2));

        assertThat(userService.findAll())
                .containsExactlyInAnyOrder(user1, user2);
    }

    @DisplayName("должен удалять пользователя")
    @Test
    void shouldDeleteUser() {
        userService.deleteUser(USERNAME);
        verify(jdbcUserDetailsManager).deleteUser(USERNAME);
    }

    @DisplayName("должен создавать UserData по UserDetails")
    @Test
    void shouldCreateByUserDetails() {
        UserDetails userDetails = mock(UserDetails.class);
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_EDITOR"));
        doReturn(authorities).when(userDetails).getAuthorities();
        doReturn(USERNAME).when(userDetails).getUsername();

        assertThat(userService.getUserByUserDetails(userDetails))
                .hasFieldOrPropertyWithValue("username", USERNAME)
                .hasFieldOrPropertyWithValue("password", null)
                .satisfies(userData -> assertThat(userData.getAuthorities()).containsExactly(UserRole.EDITOR));
    }
}