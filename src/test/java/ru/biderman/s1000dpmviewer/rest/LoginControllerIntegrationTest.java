package ru.biderman.s1000dpmviewer.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.biderman.s1000dpmviewer.testutils.SecurityTestUtils.*;

@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
@DisplayName("Контроллер логина при интеграционном тесте ")
public class LoginControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig{
        @Primary
        @Bean
        public EhCacheManagerFactoryBean aclCacheManager() {
            EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
            factoryBean.setShared(false);
            factoryBean.setAcceptExisting(true);
            return factoryBean;
        }
    }

    @DisplayName("должен возвращать текущего пользователя при правильной аутентификации")
    @Test
    void shouldGetCurrentUserIfAuthenticationCorrect() throws Exception{
        mockMvc.perform(post("/login")
                .with(csrf())
                .header("Authorization", getAdminAuthorizationHeader())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(ADMIN_USERNAME))
                .andExpect(jsonPath("$.authorities").isArray())
                .andReturn();
    }

    @DisplayName("должен возвращать ошибку при неправильной аутентификации")
    @Test
    void shouldGetErrorIfAuthenticationNotCorrect() throws Exception {
        mockMvc.perform(post("/login")
                .with(csrf())
                .header("Authorization", getAuthorizationHeader("Invalid username", "Invalid password"))
        )
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
