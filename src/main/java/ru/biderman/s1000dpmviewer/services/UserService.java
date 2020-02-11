package ru.biderman.s1000dpmviewer.services;

import org.springframework.security.core.userdetails.UserDetails;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.exceptions.InvalidUsernameException;
import ru.biderman.s1000dpmviewer.exceptions.UserAlreadyExistsException;

import java.util.List;

public interface UserService {
    List<UserData> findAll();
    void createUser(UserData userData) throws UserAlreadyExistsException, InvalidUsernameException;
    void deleteUser(String username);
    // TODO добавить редактирование (целиком или отдельно смену пароля, отдельно смену ролей?)

    UserData getUserByUserDetails(UserDetails userDetails);
}
