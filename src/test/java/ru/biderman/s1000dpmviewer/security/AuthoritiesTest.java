package ru.biderman.s1000dpmviewer.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class AuthoritiesTest {
    @DisplayName("должен получать множество Authorities по коллекции")
    @ParameterizedTest
    @MethodSource("authoritiesCollectionData")
    void shouldGetAuthoritiesSetByCollection(Collection<GrantedAuthority> authorityCollection, Authorities[] authorities) {
        assertThat(Authorities.getAuthorities(authorityCollection))
                .containsExactlyInAnyOrder(authorities);
    }

    private static Stream<Arguments> authoritiesCollectionData() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), new Authorities[]{}),

                Arguments.of(Collections.singletonList(new SimpleGrantedAuthority("ROLE_EDITOR")),
                        new Authorities[]{Authorities.EDITOR}
                ),

                Arguments.of(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_EDITOR"),
                        new SimpleGrantedAuthority("ROLE_SOMETHING_ELSE")
                    ),
                        new Authorities[]{Authorities.EDITOR, Authorities.ADMIN}
                )
        );
    }
}