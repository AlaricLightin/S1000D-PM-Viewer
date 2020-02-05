package ru.biderman.s1000dpmviewer.postgres_integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.services.PublicationService;
import ru.biderman.s1000dpmviewer.testutils.TestConsts;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.biderman.s1000dpmviewer.testutils.TestConsts.*;
import static ru.biderman.s1000dpmviewer.testutils.TestUtils.getDataFile;

@SpringBootTest
@DisplayName("Сервис работы с публикациями (при интеграционном тесте с использованием БД Postgres) ")
@ActiveProfiles("postgres-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PublicationServiceImplTest {
    @Autowired
    PublicationService publicationService;

    @DisplayName("должен создавать публикацию")
    @Test
    void shouldCreatePublication() throws Exception{
        try(
                FileInputStream fileInputStream = new FileInputStream(getDataFile(TestConsts.TEST_PUBLICATION_FILENAME))
        ) {
            Publication publication = publicationService.add(fileInputStream);
            assertThat(publication)
                    .satisfies(p -> assertThat(p.getId()).isGreaterThan(0))
                    .satisfies(p -> assertThat(p.getXml()).contains(TestConsts.TEST_PUBLICATION_CODE_XML_STRING))
                    .satisfies(p -> assertThat(p.getDetails())
                            .hasFieldOrPropertyWithValue("code", TEST_PUBLICATION_CODE)
                            .hasFieldOrPropertyWithValue("issue", TEST_PUBLICATION_ISSUE)
                            .hasFieldOrPropertyWithValue("language", TEST_PUBLICATION_LANGUAGE)
                    );
        }
    }
}