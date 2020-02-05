package ru.biderman.s1000dpmviewer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.biderman.s1000dpmviewer.security.Authorities;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class User {
    private final String username;
    private final String password;
    private final Set<Authorities> authorities;
}
