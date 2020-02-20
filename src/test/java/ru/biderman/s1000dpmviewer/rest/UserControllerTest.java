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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import ru.biderman.s1000dpmviewer.domain.UserData;
import ru.biderman.s1000dpmviewer.domain.UserRole;
import ru.biderman.s1000dpmviewer.exceptions.*;
import ru.biderman.s1000dpmviewer.services.UserService;
import ru.biderman.s1000dpmviewer.testutils.WithMockAdmin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final Set<UserRole> TEST_USER_ROLES = Collections.singleton(UserRole.EDITOR);

    private static UserData createTestUserData() {
        return new UserData(USERNAME, PASSWORD, TEST_USER_ROLES);
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
        mockMvc.perform(delete("/user/{username}", username).with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        verify(userService).deleteUser(username);
    }

    @DisplayName("не должен удалять текущего пользователя")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldNotDeleteCurrentUser() throws Exception{
        mockMvc.perform(delete("/user/{username}", "admin").with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCodes.CANNOT_DELETE_CURRENT_USER))
                .andReturn();
    }

    @DisplayName("должен менять пользователю пароль")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldChangePassword() throws Exception{
        UserData userData = new UserData(USERNAME, PASSWORD, null);

        mockMvc.perform(put("/user/{username}", USERNAME)
                .content(createTestUserJsonBytes(userData))
                .contentType(APPLICATION_JSON_VALUE)
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andReturn();

        verify(userService).changeUserPassword(USERNAME, PASSWORD);
    }

    @DisplayName("должен менять пользователю роли")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldChangeRoles() throws Exception{
        Set<UserRole> newRoles = Collections.singleton(UserRole.ADMIN);
        UserData userData = new UserData(USERNAME, null, newRoles);

        mockMvc.perform(put("/user/{username}", USERNAME)
                .content(createTestUserJsonBytes(userData))
                .contentType(APPLICATION_JSON_VALUE)
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andReturn();

        verify(userService).changeUserRoles(USERNAME, newRoles);
    }

    @DisplayName("должен сообщать об ошибках, если редактирование приводит к ошибкам")
    @ParameterizedTest
    @MethodSource("dataForEditingError")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldThrowExceptionIfErrorWhenEditing(Class<Exception> exceptionClass,
                                                ResultMatcher resultMatcher) throws Exception {
        UserData userData = new UserData(USERNAME, PASSWORD, null);
        doThrow(exceptionClass).when(userService).changeUserPassword(USERNAME, PASSWORD);

        mockMvc.perform(put("/user/{username}", USERNAME)
                .content(createTestUserJsonBytes(userData))
                .contentType(APPLICATION_JSON_VALUE)
                .with(csrf())
        )
                .andExpect(resultMatcher)
                .andReturn();
    }

    private static Stream<Arguments> dataForEditingError() {
        return Stream.of(
                Arguments.of(UserNotFoundException.class, status().isNotFound()),
                Arguments.of(InvalidPasswordException.class, status().isBadRequest())
        );
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
                Arguments.of(post("/user")
                        .content(createTestUserJsonBytes(userData))
                        .contentType(APPLICATION_JSON_VALUE)
                        .with(csrf())),
                Arguments.of(delete("/user/admin").with(csrf())),
                Arguments.of(put("/user")
                        .content(createTestUserJsonBytes(userData))
                        .contentType(APPLICATION_JSON_VALUE)
                        .with(csrf()))
        );
    }
}