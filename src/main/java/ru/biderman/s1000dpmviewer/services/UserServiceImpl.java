package ru.biderman.s1000dpmviewer.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.biderman.s1000dpmviewer.domain.User;
import ru.biderman.s1000dpmviewer.security.Authorities;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserDetails(UserDetails userDetails) {
        String username = userDetails.getUsername();
        Set<Authorities> authorities = Authorities.getAuthorities(userDetails.getAuthorities());

        return new User(username, null, authorities);
    }
}
