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
import ru.biderman.s1000dpmviewer.security.AuthorityUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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

    private static Stream<Arguments> dataForCreateTest() {
        return Stream.of(
                Arguments.of("username1", Arrays.asList(UserRole.EDITOR, UserRole.ADMIN)),
                Arguments.of("username2", null)
        );
    }
}