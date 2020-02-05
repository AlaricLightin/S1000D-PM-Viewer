package ru.biderman.s1000dpmviewer.postgres_integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import ru.biderman.s1000dpmviewer.testutils.WithMockEditor;

import java.io.FileInputStream;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PublicationControllerTest {
    private static final long START_ID = 100;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PublicationDetailsService detailsService;

    @Autowired
    PublicationService publicationService;

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

        Optional<PublicationDetails> details = detailsService.findById(START_ID);
        assertThat(details)
                .isPresent();

        assertThat(details.get())
                .hasFieldOrPropertyWithValue("id", START_ID)
                .hasFieldOrPropertyWithValue("code", TEST_PUBLICATION_CODE)
                .hasFieldOrPropertyWithValue("issue", TEST_PUBLICATION_ISSUE)
                .hasFieldOrPropertyWithValue("language", TEST_PUBLICATION_LANGUAGE)
                .hasFieldOrPropertyWithValue("title", TEST_PUBLICATION_TITLE);
    }

    private void addTestPublication() throws Exception {
        try (
                FileInputStream fileInputStream = new FileInputStream(TestUtils.getDataFile(TestConsts.TEST_PUBLICATION_FILENAME))
        ) {
            publicationService.add(fileInputStream);
        }
    }

    @DisplayName("должен проверять перечень публикаций")
    @Test
    void shouldGetPublications() throws Exception {
        addTestPublication();

        mockMvc.perform(get("/publication").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(START_ID))
                .andExpect(jsonPath("$[0].code").value(TEST_PUBLICATION_CODE))
                .andExpect(jsonPath("$[0].issue").value(TEST_PUBLICATION_ISSUE))
                .andExpect(jsonPath("$[0].language").value(TEST_PUBLICATION_LANGUAGE))
                .andReturn();
    }

    @DisplayName("должен возвращать контент")
    @Test
    void shouldGetContent() throws Exception {
        addTestPublication();

        mockMvc.perform(get("/publication/{id}/content", START_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(TEST_PUBLICATION_TITLE))
                .andExpect(jsonPath("$[0].children[0].name").value(TEST_SECTION_TITLE))
                .andExpect(jsonPath("$[0].children[0].children[0].name").value(containsString(TEST_DM_CODE)))
                .andReturn();
    }

    @DisplayName("должен удалять публикацию (с проверкой безопасности)")
    @Test
    void shouldDeletePublication() throws Exception {
        addTestPublication();

        mockMvc.perform(delete("/publication/{id}", START_ID)
                .with(csrf())
                .header("Authorization", getAdminAuthorizationHeader())
        )
                .andExpect(status().isOk())
                .andReturn();

        Optional<PublicationDetails> details = detailsService.findById(START_ID);
        assertThat(details)
                .isEmpty();
    }
}
