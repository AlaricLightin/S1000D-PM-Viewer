package ru.biderman.s1000dpmviewer.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.services.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@Import(ControllerTestConfiguration.class)
@DisplayName("Контроллер логина")
class LoginControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @DisplayName("должен возвращать текущего пользователя")
    @Test
    @WithMockUser
    void shouldGetCurrentUser() throws Exception{
        UserData userData = new UserData("username", null, Collections.singleton(UserRole.EDITOR));
        when(userService.getUserByUserDetails(any())).thenReturn(userData);

        mockMvc.perform(post("/login").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userData.getUsername()))
                .andExpect(jsonPath("$.authorities").isArray())
                .andExpect(jsonPath("$.authorities[0]").value(UserRole.EDITOR.toString()))
                .andReturn();
    }
}