package ru.biderman.s1000dpmviewer.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.exceptions.CustomBadRequestException;
import ru.biderman.s1000dpmviewer.exceptions.EmptyPasswordException;
import ru.biderman.s1000dpmviewer.exceptions.InvalidUsernameException;
import ru.biderman.s1000dpmviewer.exceptions.UserAlreadyExistsException;
import ru.biderman.s1000dpmviewer.repositories.UserDao;
import ru.biderman.s1000dpmviewer.security.AuthorityUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final JdbcUserDetailsManager jdbcUserDetailsManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserData getUserByUserDetails(UserDetails userDetails) {
        String username = userDetails.getUsername();
        Set<UserRole> authorities = AuthorityUtils.getAuthorities(userDetails.getAuthorities());

        return new UserData(username, null, authorities);
    }

    @Override
    public List<UserData> findAll() {
        return userDao.findAllUsers();
    }

    @Override
    @Transactional
    public void createUser(UserData userData) throws CustomBadRequestException {
        String username = userData.getUsername();
        if (username == null || username.isEmpty())
            throw new InvalidUsernameException();

        String password = userData.getPassword();
        if (password == null || password.isEmpty())
            throw new EmptyPasswordException();

        if (jdbcUserDetailsManager.userExists(username))
            throw new UserAlreadyExistsException();

        ArrayList<String> roleList;
        if (userData.getAuthorities() != null)
            roleList = userData.getAuthorities().stream()
                    .map(UserRole::toString)
                    .collect(Collectors.toCollection(ArrayList::new));
        else
            roleList = new ArrayList<>();

        if(roleList.size() == 0)
            roleList.add("USER");
        String[] roles = roleList.toArray(new String[]{});

        UserDetails newUser = User.builder()
                .username(userData.getUsername())
                .password(userData.getPassword())
                .passwordEncoder(passwordEncoder::encode)
                .roles(roles)
                .build();

        jdbcUserDetailsManager.createUser(newUser);
    }

    @Override
    public void deleteUser(String username) {
        jdbcUserDetailsManager.deleteUser(username);
    }
}
