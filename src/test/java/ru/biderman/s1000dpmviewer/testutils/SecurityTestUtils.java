package ru.biderman.s1000dpmviewer.testutils;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.nio.charset.StandardCharsets;

public class SecurityTestUtils {
    public static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password";

    public static String getAuthorizationHeader(String username, String password) {
        return "Basic " + Base64.encode((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    public static String getAdminAuthorizationHeader() {
        return getAuthorizationHeader(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

}
