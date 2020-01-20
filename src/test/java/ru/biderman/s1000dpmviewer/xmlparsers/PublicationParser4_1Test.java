package ru.biderman.s1000dpmviewer.xmlparsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.utils.TestUtils;
import ru.biderman.s1000dpmviewer.utils.XMLDocumentUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.biderman.s1000dpmviewer.utils.TestConsts.*;
import static ru.biderman.s1000dpmviewer.utils.TestUtils.getDataFile;

@ExtendWith(SpringExtension.class)
@DisplayName("Парсер публикаций из формата 4.1 ")
class PublicationParser4_1Test {
    private PublicationParser4_1 parser;

    @BeforeEach
    void init() {
        parser = new PublicationParser4_1();
    }

    @DisplayName("должен получать детали публикации")
    @Test
    void shouldGetPublicationDetails() {
        Document document = XMLDocumentUtils.getDocumentFromFile(getDataFile(TEST_PUBLICATION_FILENAME));
        assertThat(document).isNotNull();

        assertThat(parser.getPublicationDetails(document))
                .hasFieldOrPropertyWithValue("code", TEST_PUBLICATION_CODE)
                .hasFieldOrPropertyWithValue("issue", TEST_PUBLICATION_ISSUE)
                .hasFieldOrPropertyWithValue("language", TEST_PUBLICATION_LANGUAGE);
    }

    @DisplayName("должен создавать публикацию по входному потоку")
    @Test
    void shouldCreateByInputStream() throws IOException {
        Path dataPath = TestUtils.getDataPath(TEST_PUBLICATION_FILENAME);
        Publication result;
        try (
                FileInputStream fileInputStream = new FileInputStream(dataPath.toFile())
        ) {
             result = parser.createPublication(fileInputStream);
        }

        assertThat(result)
                .satisfies(publication -> assertThat(publication.getDetails())
                        .hasFieldOrPropertyWithValue("code", TEST_PUBLICATION_CODE)
                        .hasFieldOrPropertyWithValue("issue", TEST_PUBLICATION_ISSUE)
                        .hasFieldOrPropertyWithValue("language", TEST_PUBLICATION_LANGUAGE))
                .satisfies(publication -> assertThat(publication.getXml())
                        .contains(TEST_PUBLICATION_CODE_XML_STRING)
                );
    }
}