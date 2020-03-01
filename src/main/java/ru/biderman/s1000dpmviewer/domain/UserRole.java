package ru.biderman.s1000dpmviewer.domain;

import java.util.Optional;

public enum UserRole {
    ADMIN, EDITOR;

    public static Optional<UserRole> getByAuthorityString(String authority) {
        Optional<UserRole> result = Optional.empty();
        for(UserRole role: values()) {
            String roleString = "ROLE_" + role.toString();
            if (roleString.equals(authority)) {
                result = Optional.of(role);
                break;
            }
        }
        return result;
    }
}
