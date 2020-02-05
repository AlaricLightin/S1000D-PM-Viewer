package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.testutils.TestConsts;
import ru.biderman.s1000dpmviewer.xmlparsers.PublicationParser4_1;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.biderman.s1000dpmviewer.testutils.TestConsts.*;
import static ru.biderman.s1000dpmviewer.testutils.TestUtils.getDataFile;

@DataJpaTest
@Import({PublicationServiceImpl.class, PublicationParser4_1.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DisplayName("Сервис работы с публикациями (при интеграционном тесте) ")
class PublicationServiceImplIntegrationTest {
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