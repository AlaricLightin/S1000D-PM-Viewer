package ru.biderman.s1000dpmviewer.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.exceptions.CannotDeleteCurrentUserException;
import ru.biderman.s1000dpmviewer.exceptions.CustomBadRequestException;
import ru.biderman.s1000dpmviewer.exceptions.InvalidPasswordException;
import ru.biderman.s1000dpmviewer.exceptions.UserNotFoundException;
import ru.biderman.s1000dpmviewer.services.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user")
    public List<UserData> findAllUsers() {
        return userService.findAll();
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody UserData userData, UriComponentsBuilder builder)
            throws CustomBadRequestException {
        userService.createUser(userData);
        UriComponents uriComponents = builder.path("/user/{id}").buildAndExpand(userData.getUsername());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PutMapping("/user/{username}")
    public void updateUser(@PathVariable("username") String username, @RequestBody UserData userData)
            throws InvalidPasswordException, UserNotFoundException {
        if(userData.getPassword() != null)
            userService.changeUserPassword(username, userData.getPassword());
        else
            userService.changeUserRoles(username, userData.getAuthorities());
    }

    @DeleteMapping("/user/{username}")
    public void deleteUser(@PathVariable("username") String username) throws CannotDeleteCurrentUserException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assert principal instanceof UserDetails;
        if (((UserDetails) principal).getUsername().equals(username))
            throw new CannotDeleteCurrentUserException();
        userService.deleteUser(username);
    }
}
