package ru.biderman.s1000dpmviewer.repositories;

import ru.biderman.s1000dpmviewer.domain.UserData;

import java.util.List;

public interface UserDao {
    List<UserData> findAllUsers();
}
