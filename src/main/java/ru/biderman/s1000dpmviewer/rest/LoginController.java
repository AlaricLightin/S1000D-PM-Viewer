package ru.biderman.s1000dpmviewer.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.biderman.s1000dpmviewer.domain.User;
import ru.biderman.s1000dpmviewer.services.UserService;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    // В настоящее время при логине просто передаются логин и пароль по принципам базовой аутентификации
    // Назад возвращается имя и права
    @PostMapping("/login")
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assert principal instanceof UserDetails;
        return userService.getUserByUserDetails((UserDetails) principal);
    }
}
