package ru.biderman.s1000dpmviewer.security;

import org.springframework.security.core.GrantedAuthority;
import ru.biderman.s1000dpmviewer.domain.UserRole;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AuthorityUtils {

    public static Set<UserRole> getAuthorities(Collection<? extends GrantedAuthority> authorities) {
        HashSet<UserRole> result = new HashSet<>();
        authorities.forEach(grantedAuthority ->
                UserRole.getByAuthorityString(grantedAuthority.getAuthority())
                .map(result::add));
        return result;
    }
}
