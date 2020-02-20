package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.exceptions.InvalidPasswordException;
import ru.biderman.s1000dpmviewer.exceptions.InvalidUsernameException;
import ru.biderman.s1000dpmviewer.exceptions.UserAlreadyExistsException;
import ru.biderman.s1000dpmviewer.exceptions.UserNotFoundException;
import ru.biderman.s1000dpmviewer.repositories.UserDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Сервис пользователей ")
class UserServiceImplTest {
    private UserServiceImpl userService;
    private UserDao userDao;
    private JdbcUserDetailsManager jdbcUserDetailsManager;
    private PasswordEncoder passwordEncoder;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private UserDetails createMockUserDetails() {
        return User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .roles("EDITOR")
                .build();
    }

    @BeforeEach
    void init() {
        userDao = mock(UserDao.class);
        jdbcUserDetailsManager = mock(JdbcUserDetailsManager.class);
        passwordEncoder = mock(PasswordEncoder.class);
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
        UserDetails userDetails = createMockUserDetails();
        assertThat(userService.getUserByUserDetails(userDetails))
                .hasFieldOrPropertyWithValue("username", USERNAME)
                .hasFieldOrPropertyWithValue("password", null)
                .satisfies(userData -> assertThat(userData.getAuthorities()).containsExactly(UserRole.EDITOR));
    }

    @DisplayName("должен создавать пользователя")
    @Test
    void shouldCreateUser() throws Exception{
        UserData userData = new UserData(USERNAME, PASSWORD, Collections.singleton(UserRole.EDITOR));
        final String encodedPassword = "Encoded password";
        when(jdbcUserDetailsManager.userExists(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(encodedPassword);

        userService.createUser(userData);

        ArgumentCaptor<UserDetails> userDetailsArgumentCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(jdbcUserDetailsManager).createUser(userDetailsArgumentCaptor.capture());
        assertThat(userDetailsArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("username", USERNAME)
                .hasFieldOrPropertyWithValue("password", encodedPassword)
                .satisfies(userDetails -> assertThat(userDetails.getAuthorities())
                        .extracting(Object::toString)
                        .containsOnly("ROLE_EDITOR"));
    }

    @DisplayName("должен бросать исключение, если создаваемый пользователь уже существует")
    @Test
    void shouldThrowExceptionIfCreatedUserExists() {
        UserData userData = new UserData(USERNAME, PASSWORD, Collections.singleton(UserRole.EDITOR));
        when(jdbcUserDetailsManager.userExists(USERNAME)).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userData));
    }
    
    @DisplayName("должен бросать исключение, если при создании пользователя неверны логин или пароль")
    @ParameterizedTest
    @MethodSource("dataForInvalidUsernameAndPassword")
    void shouldThrowExceptionIfInvalidUsernameOrPassword(String username, String password, Class<Exception> exceptionClass) {
        UserData userData = new UserData(username, password, Collections.emptySet());
        when(jdbcUserDetailsManager.userExists(USERNAME)).thenReturn(false);
        assertThrows(exceptionClass, () -> userService.createUser(userData));
    }
    
    private static Stream<Arguments> dataForInvalidUsernameAndPassword() {
        return Stream.of(
                Arguments.of(null, PASSWORD, InvalidUsernameException.class),
                Arguments.of("", PASSWORD, InvalidUsernameException.class),
                Arguments.of(",!@", PASSWORD, InvalidUsernameException.class),
                Arguments.of("user1", "Пароль из кириллицы", InvalidPasswordException.class),
                Arguments.of("user1", null, InvalidPasswordException.class),
                Arguments.of("user1", "", InvalidPasswordException.class)
        );
    }

    @DisplayName("должен менять пользователю пароль")
    @Test
    void shouldChangePassword() throws Exception {
        UserDetails userDetails = createMockUserDetails();
        when(jdbcUserDetailsManager.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        final String newPassword = "NewPassword";
        final String encodedPassword = "Encoded Password";
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        userService.changeUserPassword(USERNAME, newPassword);

        ArgumentCaptor<UserDetails> userDetailsArgumentCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(jdbcUserDetailsManager).updateUser(userDetailsArgumentCaptor.capture());
        assertThat(userDetailsArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("username", USERNAME)
                .hasFieldOrPropertyWithValue("password", encodedPassword)
                .satisfies(ud -> assertThat(ud.getAuthorities())
                        .extracting(Object::toString)
                        .containsOnly("ROLE_EDITOR"));
    }

    @DisplayName("должен менять пользователю роли")
    @Test
    void shouldChangeRoles() throws Exception {
        UserDetails userDetails = createMockUserDetails();
        when(jdbcUserDetailsManager.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        userService.changeUserRoles(USERNAME, Collections.singleton(UserRole.ADMIN));

        ArgumentCaptor<UserDetails> userDetailsArgumentCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(jdbcUserDetailsManager).updateUser(userDetailsArgumentCaptor.capture());
        assertThat(userDetailsArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("username", USERNAME)
                .hasFieldOrPropertyWithValue("password", PASSWORD)
                .satisfies(ud -> assertThat(ud.getAuthorities())
                        .extracting(Object::toString)
                        .containsOnly("ROLE_ADMIN"));
    }

    @DisplayName("должен бросать исключение, если редактируемого пользователя нет")
    @Test
    void shouldThrowExceptionIfEditedUserNotExists() {
        doThrow(NotFoundException.class).when(jdbcUserDetailsManager).loadUserByUsername(USERNAME);
        assertThrows(UserNotFoundException.class, () -> userService.changeUserPassword(USERNAME, PASSWORD));
    }

    @DisplayName("должен бросать исключение, если новый пароль неверен")
    @Test
    void shouldThrowExceptionIfInvalidNewPassword() {
        UserDetails userDetails = createMockUserDetails();
        when(jdbcUserDetailsManager.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        final String invalidPassword = "абвгд";
        assertThrows(InvalidPasswordException.class, () -> userService.changeUserPassword(USERNAME, invalidPassword));
    }
}