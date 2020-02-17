package ru.biderman.s1000dpmviewer.postgres_integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.services.PublicationDetailsService;
import ru.biderman.s1000dpmviewer.services.PublicationService;
import ru.biderman.s1000dpmviewer.testutils.TestConsts;
import ru.biderman.s1000dpmviewer.testutils.TestUtils;
import ru.biderman.s1000dpmviewer.testutils.WithMockAdmin;
import ru.biderman.s1000dpmviewer.testutils.WithMockEditor;

import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.biderman.s1000dpmviewer.testutils.SecurityTestUtils.getAdminAuthorizationHeader;
import static ru.biderman.s1000dpmviewer.testutils.TestConsts.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Контроллер публикации при интеграционном тесте ")
@ActiveProfiles("postgres-test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PublicationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PublicationDetailsService detailsService;

    @Autowired
    PublicationService publicationService;

    @Order(0)
    @DisplayName("должен добавлять публикацию из файла")
    @Test
    @WithMockEditor
    void shouldAdd() throws Exception{
        try (
                FileInputStream fileInputStream = new FileInputStream(TestUtils.getDataFile(TestConsts.TEST_PUBLICATION_FILENAME))
        ) {
            MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);
            mockMvc.perform(multipart("/publication").file(multipartFile).with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();
        }
    }

    @Order(1)
    @DisplayName("должен проверять, что публикация записалась")
    @Test
    @WithMockAdmin
    void shouldCheckPreviousWrite() {
        Optional<PublicationDetails> details = detailsService.findById(START_PUBLICATION_ID);
        assertThat(details)
                .isPresent();

        assertThat(details.get())
                .hasFieldOrPropertyWithValue("id", START_PUBLICATION_ID)
                .hasFieldOrPropertyWithValue("code", TEST_PUBLICATION_CODE)
                .hasFieldOrPropertyWithValue("issue", TEST_PUBLICATION_ISSUE)
                .hasFieldOrPropertyWithValue("language", TEST_PUBLICATION_LANGUAGE)
                .hasFieldOrPropertyWithValue("title", TEST_PUBLICATION_TITLE);
    }

    @Order(2)
    @DisplayName("должен проверять перечень публикаций (с проверкой безопасности)")
    @Test
    void shouldGetPublications() throws Exception {
        mockMvc.perform(get("/publication").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        mockMvc.perform(get("/publication")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAdminAuthorizationHeader())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(START_PUBLICATION_ID))
                .andExpect(jsonPath("$[0].code").value(TEST_PUBLICATION_CODE))
                .andExpect(jsonPath("$[0].issue").value(TEST_PUBLICATION_ISSUE))
                .andExpect(jsonPath("$[0].language").value(TEST_PUBLICATION_LANGUAGE))
                .andReturn();
    }

    @Order(3)
    @DisplayName("должен возвращать контент (с проверкой безопасности)")
    @Test
    void shouldGetContent() throws Exception {
        mockMvc.perform(get("/publication/{id}/content", START_PUBLICATION_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        mockMvc.perform(get("/publication/{id}/content", START_PUBLICATION_ID)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getAdminAuthorizationHeader())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(TEST_PUBLICATION_TITLE))
                .andExpect(jsonPath("$[0].children[0].name").value(TEST_SECTION_TITLE))
                .andExpect(jsonPath("$[0].children[0].children[0].name").value(containsString(TEST_DM_CODE)))
                .andReturn();
    }

    @Order(4)
    @DisplayName("должен удалять публикацию (с проверкой безопасности)")
    @Test
    void shouldDeletePublication() throws Exception {
        mockMvc.perform(delete("/publication/{id}", START_PUBLICATION_ID)
                        .with(csrf())
        )
                .andExpect(status().isUnauthorized())
                .andReturn();

        mockMvc.perform(delete("/publication/{id}", START_PUBLICATION_ID)
                .with(csrf())
                .header("Authorization", getAdminAuthorizationHeader())
        )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Order(5)
    @DisplayName("должен проверять, что после удаления ничего не осталось")
    @WithMockAdmin
    @Test
    void shouldCheckIfDeletingSucceeded() {
        List<PublicationDetails> details = detailsService.findAll();
        assertThat(details)
                .isEmpty();
    }
}
