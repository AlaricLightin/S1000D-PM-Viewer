package ru.biderman.s1000dpmviewer.testutils;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(roles="EDITOR", username = "editor")
public @interface WithMockEditor {
}
