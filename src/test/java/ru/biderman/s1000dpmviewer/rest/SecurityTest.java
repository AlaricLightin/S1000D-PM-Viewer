package ru.biderman.s1000dpmviewer.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import ru.biderman.s1000dpmviewer.services.PublicationDetailsService;
import ru.biderman.s1000dpmviewer.services.PublicationService;

import javax.sql.DataSource;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.biderman.s1000dpmviewer.rest.ControllerTestUtils.createTestMultipartFile;

@WebMvcTest(PublicationController.class)
@DisplayName("Проверка безопасности работы с публикациями ")
public class SecurityTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PublicationDetailsService detailsService;

    @MockBean
    PublicationService publicationService;

    @MockBean
    ContentDtoService contentDtoService;

    @MockBean
    DataSource dataSource;

    private static final long PUB_ID = 101;

    @Nested
    @WithMockUser
    @DisplayName("должна блокировать при отсутствии прав ")
    class BlockNonAuthorizedUser {
        @DisplayName("при попытке добавить публикацию")
        @Test
        void shouldForbidUpload() throws Exception {
            final String content = "Test stream";
            MockMultipartFile multipartFile = createTestMultipartFile(content);
            mockMvc.perform(multipart("/publication").file(multipartFile).with(csrf()))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }

        @DisplayName("при попытке удалить публикацию")
        @ParameterizedTest
        @ArgumentsSource(Users.class)
        void shouldForbidDeletePublication(RequestPostProcessor user) throws Exception{
            mockMvc.perform(delete("/publication/{id}", PUB_ID).with(user).with(csrf()))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }
    }

    static class Users implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(user("user")),
                    Arguments.of(user("user").roles("EDITOR"))
            );
        }
    }
}
