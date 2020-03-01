package ru.biderman.s1000dpmviewer.services;

import ru.biderman.s1000dpmviewer.domain.UserRole;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class UserUtils {
    private static final Pattern userPattern = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final Pattern passwordPattern = Pattern.compile("^[A-Za-z0-9,.!@#$%^&*()-_+=]+$");

    static boolean checkUsername(String username) {
        return username != null && !username.isEmpty() && userPattern.matcher(username).matches();
    }

    static boolean checkPassword(String password) {
        return password != null && !password.isEmpty() && passwordPattern.matcher(password).matches();
    }

    static String[] getRoleStringList(Set<UserRole> userRoleSet) {
        ArrayList<String> result;
        if (userRoleSet != null)
            result = userRoleSet.stream()
                    .map(UserRole::toString)
                    .collect(Collectors.toCollection(ArrayList::new));
        else
            result = new ArrayList<>();

        if(result.size() == 0)
            result.add("USER");

        return result.toArray(new String[]{});
    }
}
