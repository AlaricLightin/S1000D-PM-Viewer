package ru.biderman.s1000dpmviewer.xmlparsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.exceptions.InvalidPublicationException;
import ru.biderman.s1000dpmviewer.testutils.TestUtils;
import ru.biderman.s1000dpmviewer.utils.XMLDocumentUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.biderman.s1000dpmviewer.testutils.TestConsts.*;
import static ru.biderman.s1000dpmviewer.testutils.TestUtils.getDataFile;

@ExtendWith(SpringExtension.class)
@DisplayName("Парсер публикаций из формата 4.1 ")
class PublicationParser4_1Test {
    private PublicationParser4_1 parser;

    private static final String PUBLICATION_TITLE = "Тестовая публикация";
    private static final String SECTION_TITLE = "Название секции";
    private static final LocalDate PUBLICATION_DATE = LocalDate.of(2020, 1, 10);

    @BeforeEach
    void init() {
        parser = new PublicationParser4_1();
    }

    @DisplayName("должен получать детали публикации")
    @Test
    void shouldGetPublicationDetails() throws Exception{
        Document document = XMLDocumentUtils.getDocumentFromFile(getDataFile(TEST_PUBLICATION_FILENAME));
        assertThat(document).isNotNull();

        assertThat(parser.getPublicationDetails(document))
                .hasFieldOrPropertyWithValue("code", TEST_PUBLICATION_CODE)
                .hasFieldOrPropertyWithValue("issue", TEST_PUBLICATION_ISSUE)
                .hasFieldOrPropertyWithValue("language", TEST_PUBLICATION_LANGUAGE)
                .hasFieldOrPropertyWithValue("title", PUBLICATION_TITLE)
                .hasFieldOrPropertyWithValue("issueDate", PUBLICATION_DATE);
    }

    @DisplayName("должен создавать публикацию по входному потоку")
    @Test
    void shouldCreateByInputStream() throws Exception {
        ZonedDateTime currentTime = ZonedDateTime.now();
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
                        .hasFieldOrPropertyWithValue("language", TEST_PUBLICATION_LANGUAGE)
                        .satisfies(details -> assertThat(details.getLoadDateTime()).isAfterOrEqualTo(currentTime))
                )
                .satisfies(publication -> assertThat(publication.getXml())
                        .contains(TEST_PUBLICATION_CODE_XML_STRING)
                );
    }

    @DisplayName("должен возвращать ошибку при неверных входных данных")
    @ParameterizedTest
    @ValueSource(strings = {
            "Invalid data",
            "<pm></pm>",
            "<pm><pmIdentAndStatusSection/></pm>",
            "<pm><pmIdentAndStatusSection><pmIdent></pmIdent></pmIdentAndStatusSection></pm>"
    })
    void shouldThrowExceptionIfInvalidPublicationData(String data) {
        InputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        assertThrows(InvalidPublicationException.class, () -> parser.createPublication(inputStream));
    }

    @DisplayName("должен получать контент публикации")
    @Test
    void shouldGetContent() {
        Document document = XMLDocumentUtils.getDocumentFromFile(getDataFile(TEST_PUBLICATION_FILENAME));
        assertThat(document).isNotNull();

        assertThat(parser.getPublicationContent(document))
                .hasFieldOrPropertyWithValue("title", PUBLICATION_TITLE)
                .satisfies(entry -> assertThat(entry.getChildren())
                        .hasSize(1)
                        .allSatisfy(entryElement -> assertThat(entryElement)
                                .isInstanceOf(Entry.class)
                                .hasFieldOrPropertyWithValue("title", SECTION_TITLE)
                        )
                );
    }
}