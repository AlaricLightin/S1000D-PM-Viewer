package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.exceptions.EmptyPasswordException;
import ru.biderman.s1000dpmviewer.exceptions.InvalidUsernameException;
import ru.biderman.s1000dpmviewer.exceptions.UserAlreadyExistsException;
import ru.biderman.s1000dpmviewer.security.AuthorityUtils;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DisplayName("Сервис по работе с пользователями при интеграционном тесте ")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserServiceImplIntegrationTest {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    JdbcUserDetailsManager userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final static String PASSWORD = "password";

    @DisplayName("должен создавать нового")
    @ParameterizedTest
    @MethodSource("dataForCreateTest")
    void shouldCreate(String username, List<UserRole> roles) throws Exception{
        Set<UserRole> roleSet = roles != null ? new HashSet<>(roles) : null;
        UserData userData = new UserData(username, PASSWORD, roleSet);
        userService.createUser(userData);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        assertThat(userDetails)
                .satisfies(details -> assertThat(details.getUsername()).isEqualTo(username))
                .satisfies(details -> assertThat(passwordEncoder.matches(PASSWORD, details.getPassword()))
                        .isEqualTo(true))
                .satisfies(details -> {
                    if (roles != null)
                        assertThat(AuthorityUtils.getAuthorities(details.getAuthorities()))
                                .containsExactlyInAnyOrder(roles.toArray(new UserRole[]{}));
                });

        if (roles == null || roles.size() == 0) {
            assertThat(userDetails.getAuthorities())
                    .hasSize(1);
        }
    }

    @DisplayName("должен бросать исключение, если не удалось создать нового пользователя")
    @ParameterizedTest
    @MethodSource("dataForCreateErrorTest")
    void shouldThrowExceptionIfCouldNotCreate(String username, String password, Set<UserRole> roles,
                                              Class<Exception> exceptionClass) {
        UserData userData = new UserData(username, password, roles);
        assertThrows(exceptionClass, () -> userService.createUser(userData));
    }

    private static Stream<Arguments> dataForCreateTest() {
        return Stream.of(
                Arguments.of("username1", Collections.emptyList()),
                Arguments.of("username2", Collections.singletonList(UserRole.EDITOR)),
                Arguments.of("username3", Arrays.asList(UserRole.EDITOR, UserRole.ADMIN)),
                Arguments.of("username4", null)
        );
    }

    private static Stream<Arguments> dataForCreateErrorTest() {
        return Stream.of(
                Arguments.of("admin", PASSWORD, Collections.singleton(UserRole.ADMIN), UserAlreadyExistsException.class),
                Arguments.of(null, PASSWORD, Collections.emptySet(), InvalidUsernameException.class),
                Arguments.of("", PASSWORD, Collections.emptySet(), InvalidUsernameException.class),
                Arguments.of("user1", null, Collections.emptySet(), EmptyPasswordException.class),
                Arguments.of("user1", "", Collections.emptySet(), EmptyPasswordException.class)
        );
    }
}