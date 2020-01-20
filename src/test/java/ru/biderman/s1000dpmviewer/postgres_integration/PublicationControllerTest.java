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
import ru.biderman.s1000dpmviewer.utils.TestConsts;
import ru.biderman.s1000dpmviewer.utils.TestUtils;

import java.io.FileInputStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.biderman.s1000dpmviewer.utils.TestConsts.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Контроллер публикации при интеграционном тесте ")
@ActiveProfiles("postgres-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PublicationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @DisplayName("должен добавлять публикацию из файла")
    @Test
    void shouldAdd() throws Exception{
        try (
                FileInputStream fileInputStream = new FileInputStream(TestUtils.getDataFile(TestConsts.TEST_PUBLICATION_FILENAME))
        ) {
            MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);
            mockMvc.perform(multipart("/publication").file(multipartFile))
                    .andExpect(status().isCreated())
                    .andReturn();
        }

        mockMvc.perform(get("/publication").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value(TEST_PUBLICATION_CODE))
                .andExpect(jsonPath("$[0].issue").value(TEST_PUBLICATION_ISSUE))
                .andExpect(jsonPath("$[0].language").value(TEST_PUBLICATION_LANGUAGE))
                .andReturn();
    }
}
