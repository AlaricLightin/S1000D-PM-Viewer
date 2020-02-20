package ru.biderman.s1000dpmviewer.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.exceptions.*;
import ru.biderman.s1000dpmviewer.repositories.UserDao;
import ru.biderman.s1000dpmviewer.security.AuthorityUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

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
        if (!UserUtils.checkUsername(username))
            throw new InvalidUsernameException();

        String password = userData.getPassword();
        if (!UserUtils.checkPassword(password))
            throw new InvalidPasswordException();

        if (jdbcUserDetailsManager.userExists(username))
            throw new UserAlreadyExistsException();

        String[] roles = UserUtils.getRoleStringList(userData.getAuthorities());

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

    @Override
    public void changeUserPassword(String username, String password) throws UserNotFoundException, InvalidPasswordException {
        editUser(username, password, null);
    }

    @Override
    public void changeUserRoles(String username, Set<UserRole> userRoleSet) throws UserNotFoundException, InvalidPasswordException {
        editUser(username, null, userRoleSet);
    }

    private void editUser(String username, String password, Set<UserRole> userRoleSet)
            throws UserNotFoundException, InvalidPasswordException {
        UserDetails userDetails;
        try {
            userDetails = jdbcUserDetailsManager.loadUserByUsername(username);
        }
        catch (NotFoundException e) {
            throw new UserNotFoundException();
        }

        User.UserBuilder newUserDetailsBuilder = User.withUserDetails(userDetails);

        if (password != null) {
            if(!UserUtils.checkPassword(password))
                throw new InvalidPasswordException();

            newUserDetailsBuilder
                    .password(password)
                    .passwordEncoder(passwordEncoder::encode);
        }
        else {
            newUserDetailsBuilder
                    .roles(UserUtils.getRoleStringList(userRoleSet));
        }

        jdbcUserDetailsManager.updateUser(newUserDetailsBuilder.build());
    }
}
