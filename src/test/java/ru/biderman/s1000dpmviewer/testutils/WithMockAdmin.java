package ru.biderman.s1000dpmviewer.testutils;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username = "admin", roles = {"ADMIN", "EDITOR"})
public @interface WithMockAdmin {
}