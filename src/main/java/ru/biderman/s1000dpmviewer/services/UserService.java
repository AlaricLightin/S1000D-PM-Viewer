package ru.biderman.s1000dpmviewer.services;

import org.springframework.security.core.userdetails.UserDetails;
import ru.biderman.s1000dpmviewer.domain.User;

public interface UserService {
    User getUserByUserDetails(UserDetails userDetails);
}
