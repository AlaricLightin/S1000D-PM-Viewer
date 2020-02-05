package ru.biderman.s1000dpmviewer.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum Authorities {
    EDITOR, ADMIN;

    public static Set<Authorities> getAuthorities(Collection<? extends GrantedAuthority> authorities) {
        HashSet<Authorities> result = new HashSet<>();
        authorities.forEach(grantedAuthority -> {
            for (Authorities value : Authorities.values()) {
                if(grantedAuthority.getAuthority().equals("ROLE_" + value.toString()))
                    result.add(value);
            }
        });
        return result;
    }
}
