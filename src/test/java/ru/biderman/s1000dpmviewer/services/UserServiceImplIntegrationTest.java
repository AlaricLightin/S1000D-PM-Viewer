package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.exceptions.InvalidUsernameException;
import ru.biderman.s1000dpmviewer.exceptions.UserAlreadyExistsException;
import ru.biderman.s1000dpmviewer.security.AuthorityUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    private final static String PASSWORD = "password";

    @DisplayName("должен создавать нового")
    @ParameterizedTest
    @MethodSource("dataForCreateTest")
    void shouldCreate(String username, List<UserRole> roles) throws Exception{
        UserData userData = new UserData(username, PASSWORD, new HashSet<>(roles));
        userService.createUser(userData);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        assertThat(userDetails)
                .satisfies(details -> assertThat(details.getUsername()).isEqualTo(username))
                .satisfies(details -> assertThat(details.getPassword()).isEqualTo(PASSWORD))
                .satisfies(details -> assertThat(AuthorityUtils.getAuthorities(details.getAuthorities()))
                        .containsExactlyInAnyOrder(roles.toArray(new UserRole[]{})));

        if (roles.size() == 0) {
            assertThat(userDetails.getAuthorities())
                    .hasSize(1);
        }
    }

    @DisplayName("должен бросать исключение, если не удалось создать нового пользователя")
    @ParameterizedTest
    @MethodSource("dataForCreateErrorTest")
    void shouldThrowExceptionIfCouldNotCreate(String username, Class<Exception> exceptionClass) {
        UserData userData = new UserData(username, PASSWORD, Collections.emptySet());
        assertThrows(exceptionClass, () -> userService.createUser(userData));
    }

    private static Stream<Arguments> dataForCreateTest() {
        return Stream.of(
                Arguments.of("username1", Collections.emptyList()),
                Arguments.of("username2", Collections.singletonList(UserRole.EDITOR)),
                Arguments.of("username3", Arrays.asList(UserRole.EDITOR, UserRole.ADMIN))
        );
    }

    private static Stream<Arguments> dataForCreateErrorTest() {
        return Stream.of(
                Arguments.of("admin", UserAlreadyExistsException.class),
                Arguments.of(null, InvalidUsernameException.class),
                Arguments.of("", InvalidUsernameException.class)
        );
    }
}