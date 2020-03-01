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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.biderman.s1000dpmviewer.domain.PublicationViewAuthorizations;
import ru.biderman.s1000dpmviewer.services.AuthorizationService;
import ru.biderman.s1000dpmviewer.testutils.WithMockAdmin;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorizationsController.class)
@Import(ControllerTestConfiguration.class)
@DisplayName("Контроллер, работающий с правами, ")
@WithMockAdmin
class AuthorizationsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorizationService authorizationService;

    private static final long PUBLICATION_ID = 201;
    private static final String USER1 = "user1";
    private static final String USER2 = "user2";

    private static PublicationViewAuthorizations createTestAuthorizations() {
        return PublicationViewAuthorizations.createForUsers(Arrays.asList(USER1, USER2));
    }

    @DisplayName("должен возвращать существующие права на публикацию")
    @Test
    void shouldGetAuthorizations() throws Exception {
        when(authorizationService.getViewAuthorizations(PUBLICATION_ID))
                .thenReturn(createTestAuthorizations());

        mockMvc.perform(get("/publication/{id}/authorizations", PUBLICATION_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permitAll").value(false))
                .andExpect(jsonPath("$.usernameList").isArray())
                .andExpect(jsonPath("$.usernameList[0]").value(USER1))
                .andExpect(jsonPath("$.usernameList[1]").value(USER2))
                .andReturn();
    }

    @DisplayName("должен принимать новые права на публикацию")
    @Test
    void shouldPutAuthorization() throws Exception {
        PublicationViewAuthorizations authorizations = createTestAuthorizations();
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(put("/publication/{id}/authorizations", PUBLICATION_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(authorizations))
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<PublicationViewAuthorizations> authorizationCaptor = ArgumentCaptor.forClass(PublicationViewAuthorizations.class);
        verify(authorizationService).setViewAuthorizations(idCaptor.capture(), authorizationCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(PUBLICATION_ID);
        assertThat(authorizationCaptor.getValue())
                .hasFieldOrPropertyWithValue("permitAll", false)
                .satisfies(a -> assertThat(a.getUsernameList()).containsExactlyInAnyOrder(USER1, USER2));
    }

    @DisplayName("должен не давать доступ без админских прав")
    @ParameterizedTest
    @MethodSource("blockUserData")
    @WithMockUser
    void shouldForbidAccess(MockHttpServletRequestBuilder requestBuilder) throws Exception{
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    private static Stream<Arguments> blockUserData() throws Exception {
        PublicationViewAuthorizations authorizations = createTestAuthorizations();
        ObjectMapper mapper = new ObjectMapper();
        return Stream.of(
                Arguments.of(get("/publication/{id}/authorizations", PUBLICATION_ID)),
                Arguments.of(put("/publication/{id}/authorizations", PUBLICATION_ID)
                        .content(mapper.writeValueAsBytes(authorizations))
                        .contentType(APPLICATION_JSON_VALUE)
                        .with(csrf()))
        );
    }

}