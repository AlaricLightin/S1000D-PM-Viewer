package ru.biderman.s1000dpmviewer.rest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@TestConfiguration
class ControllerTestConfiguration {
    @MockBean
    JdbcUserDetailsManager jdbcUserDetailsManager;
}
