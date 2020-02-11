package ru.biderman.s1000dpmviewer.repositories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.*;

@SuppressWarnings("SqlResolve")
@Repository
@RequiredArgsConstructor
public class UserDaoJdbc implements UserDao {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<UserData> findAllUsers() {
        return jdbc
                .query("SELECT username, authority FROM authorities", new AuthorityMapper())
                .stream()
                .collect(groupingBy(
                        TempAuthority::getUsername,
                        mapping(tempAuthority -> UserRole.getByAuthorityString(tempAuthority.getAuthority()).orElse(null),
                                toList())
                ))
                .entrySet()
                .stream()
                .map(entry -> new UserData(
                        entry.getKey(),
                        null,
                        entry.getValue().stream()
                                .filter(Objects::nonNull)
                                .collect(toSet())
                        )
                )
                .collect(toList());
    }

    @RequiredArgsConstructor
    @Getter
    private static class TempAuthority {
        private final String username;
        private final String authority;
    }

    private static class AuthorityMapper implements RowMapper<TempAuthority> {
        @Override
        public TempAuthority mapRow(ResultSet resultSet, int i) throws SQLException {
            return new TempAuthority(resultSet.getString("username"), resultSet.getString("authority"));
        }
    }
}
