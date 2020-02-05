package ru.biderman.s1000dpmviewer.rest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;

@TestConfiguration
class ControllerTestConfiguration {
    @MockBean
    DataSource dataSource;
}
