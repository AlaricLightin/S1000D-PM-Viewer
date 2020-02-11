package ru.biderman.s1000dpmviewer.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@DisplayName("Dao для получения всех пользователей ")
@Import(UserDaoJdbc.class)
class UserDaoJdbcTest {
    @Autowired
    UserDaoJdbc userDaoJdbc;

    @DisplayName("должно возвращать всех пользователей")
    @Test
    void shouldFindAll() {
        final HashSet<UserRole> adminRoles = new HashSet<>();
        adminRoles.add(UserRole.ADMIN);
        adminRoles.add(UserRole.EDITOR);
        final UserData admin = new UserData("admin", null, adminRoles);
        final UserData editor = new UserData("editor", null, Collections.singleton(UserRole.EDITOR));
        final UserData user = new UserData("user", null, Collections.emptySet());

        List<UserData> userList = userDaoJdbc.findAllUsers();
        assertThat(userList)
                .usingFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(admin, editor, user);
    }
}