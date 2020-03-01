package ru.biderman.s1000dpmviewer.services;

import org.springframework.security.core.userdetails.UserDetails;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.exceptions.CustomBadRequestException;
import ru.biderman.s1000dpmviewer.exceptions.InvalidPasswordException;
import ru.biderman.s1000dpmviewer.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserData> findAll();
    void createUser(UserData userData) throws CustomBadRequestException;
    void deleteUser(String username);
    void changeUserPassword(String username, String password) throws UserNotFoundException, InvalidPasswordException;
    void changeUserRoles(String username, Set<UserRole> userRoleSet) throws UserNotFoundException, InvalidPasswordException;

    UserData getUserByUserDetails(UserDetails userDetails);
}
