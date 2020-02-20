package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biderman.s1000dpmviewer.domain.UserRole;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Проверка утилит работы с пользователями")
class UserUtilsTest {
    @DisplayName("Проверка правильности имени пользователя")
    @ParameterizedTest
    @MethodSource("dataForCheckUsername")
    void shouldCheckUsername(String username, boolean isValid) {
        assertThat(UserUtils.checkUsername(username)).isEqualTo(isValid);
    }

    private static Stream<Arguments> dataForCheckUsername() {
        return Stream.of(
                Arguments.of("admin", true),
                Arguments.of("a", true),
                Arguments.of("abc34", true),
                Arguments.of(null, false),
                Arguments.of("", false),
                Arguments.of(",", false),
                Arguments.of("a,", false)
        );
    }

    @DisplayName("Проверка правильности пароля")
    @ParameterizedTest
    @MethodSource("dataForCheckPassword")
    void shouldCheckPassword(String password, boolean isValid) {
        assertThat(UserUtils.checkPassword(password)).isEqualTo(isValid);
    }

    private static Stream<Arguments> dataForCheckPassword() {
        return Stream.of(
                Arguments.of("admin", true),
                Arguments.of("a", true),
                Arguments.of(null, false),
                Arguments.of("", false),
                Arguments.of(",", true),
                Arguments.of("a,", true)
        );
    }

    @DisplayName("Проверка создания списка ролей")
    @ParameterizedTest
    @MethodSource("dataForGetRoleList")
    void shouldGetRoleStringList(Set<UserRole> userRoleSet, String[] result) {
        assertThat(UserUtils.getRoleStringList(userRoleSet)).containsExactlyInAnyOrder(result);
    }

    private static Stream<Arguments> dataForGetRoleList() {
        return Stream.of(
                Arguments.of(null, new String[]{"USER"}),
                Arguments.of(Collections.emptySet(), new String[]{"USER"}),
                Arguments.of(Collections.singleton(UserRole.EDITOR), new String[]{"EDITOR"}),
                Arguments.of(new HashSet<>(Arrays.asList(UserRole.ADMIN, UserRole.EDITOR)),
                        new String[]{"ADMIN", "EDITOR"})
        );
    }
}