package ru.biderman.s1000dpmviewer.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.exceptions.CustomBadRequestException;
import ru.biderman.s1000dpmviewer.exceptions.ErrorCodes;
import ru.biderman.s1000dpmviewer.exceptions.InvalidUsernameException;
import ru.biderman.s1000dpmviewer.exceptions.UserAlreadyExistsException;
import ru.biderman.s1000dpmviewer.services.UserService;
import ru.biderman.s1000dpmviewer.testutils.WithMockAdmin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Контроллер по работе с пользователями ")
@Import(ControllerTestConfiguration.class)
@WithMockAdmin
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    private static UserData createTestUserData() {
        return new UserData("username", "password", Collections.singleton(UserRole.EDITOR));
    }

    private static byte[] createTestUserJsonBytes(UserData userData) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(userData);
    }

    @DisplayName("должен получать всех пользователей")
    @Test
    void shouldFindAll() throws Exception{
        UserData userData1 = new UserData("username1", null,
                new HashSet<>(Arrays.asList(UserRole.ADMIN, UserRole.EDITOR)));
        UserData userData2 = new UserData("username2", null, Collections.emptySet());
        when(userService.findAll()).thenReturn(Arrays.asList(userData1, userData2));

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value(userData1.getUsername()))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].username").value(userData2.getUsername()))
                .andExpect(jsonPath("$[0].authorities",
                        containsInAnyOrder(UserRole.ADMIN.toString(), UserRole.EDITOR.toString())))
                .andExpect(jsonPath("$[1].authorities", hasSize(0)))
                .andReturn();
    }

    @DisplayName("должен добавлять пользователя")
    @Test
    void shouldCreateUser() throws Exception {
        UserData userData = createTestUserData();
        mockMvc.perform(post("/user")
                .content(createTestUserJsonBytes(userData))
                .contentType(APPLICATION_JSON_VALUE)
                .with(csrf())
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        UriComponentsBuilder.newInstance()
                                .scheme("http")
                                .host("localhost")
                                .path("/user/{username}")
                                .buildAndExpand(userData.getUsername())
                                .toUriString()
                ))
                .andReturn();


        ArgumentCaptor<UserData> argumentCaptor = ArgumentCaptor.forClass(UserData.class);
        verify(userService).createUser(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("username", userData.getUsername())
                .hasFieldOrPropertyWithValue("password", userData.getPassword())
                .satisfies(data -> assertThat(data.getAuthorities()).containsExactly(UserRole.EDITOR));
    }

    @DisplayName("должен сообщать об ошибке, если добавить пользователя нельзя")
    @ParameterizedTest
    @MethodSource("errorAddData")
    void shouldSendBadRequestIfCouldNotAdd(Class<CustomBadRequestException> exceptionClass, int errorCode) throws Exception{
        doThrow(exceptionClass).when(userService).createUser(any());

        UserData userData = createTestUserData();
        mockMvc.perform(post("/user")
                .content(createTestUserJsonBytes(userData))
                .contentType(APPLICATION_JSON_VALUE)
                .with(csrf())
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode))
                .andReturn();
    }

    private static Stream<Arguments> errorAddData() {
        return Stream.of(
                Arguments.of(UserAlreadyExistsException.class, ErrorCodes.USER_ALREADY_EXISTS),
                Arguments.of(InvalidUsernameException.class, ErrorCodes.INVALID_USERNAME)
        );
    }

    @DisplayName("должен удалять пользователя")
    @Test
    void shouldDeleteUser() throws Exception {
        final String username = "username";
        mockMvc.perform(delete("/user/{username}", username))
                .andExpect(status().isOk())
                .andReturn();

        verify(userService).deleteUser(username);
    }

    @DisplayName("не должен удалять текущего пользователя")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldNotDeleteCurrentUser() throws Exception{
        mockMvc.perform(delete("/user/{username}", "admin"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.CANNOT_DELETE_CURRENT_USER))
                .andReturn();
    }

    @DisplayName("должен не давать доступ к пользователям без админских прав")
    @ParameterizedTest
    @MethodSource("blockUserData")
    @WithMockUser
    void shouldForbidAccess(MockHttpServletRequestBuilder requestBuilder) throws Exception{
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private static Stream<Arguments> blockUserData() throws Exception {
        UserData userData = createTestUserData();
        return Stream.of(
                Arguments.of(get("/user")),
                Arguments.of(post("/user").content(createTestUserJsonBytes(userData))
                        .contentType(APPLICATION_JSON_VALUE)
                        .with(csrf())),
                Arguments.of(delete("/user/admin"))
        );
    }
}