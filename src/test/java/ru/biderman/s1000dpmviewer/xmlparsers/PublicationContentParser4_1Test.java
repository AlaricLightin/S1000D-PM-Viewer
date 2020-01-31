package ru.biderman.s1000dpmviewer.xmlparsers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.DMRef;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.utils.XMLDocumentUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.biderman.s1000dpmviewer.utils.TestConsts.*;
import static ru.biderman.s1000dpmviewer.utils.TestUtils.getDataFile;

@ExtendWith(SpringExtension.class)
@DisplayName("Класс по созданию контента публикации ")
class PublicationContentParser4_1Test {

    private static final String DM_ISSUE = "001-00";
    private static final String DM_LANGUAGE = "RU-RU";
    private static final String DM_TITLE = "Документ";

    @DisplayName("должен его создавать")
    @Test
    void shouldCreateContent() {
        Document document = XMLDocumentUtils.getDocumentFromFile(getDataFile(TEST_PUBLICATION_FILENAME));
        assertThat(document).isNotNull();

        assertThat(PublicationContentParser4_1.createRootEntry(document))
                .hasFieldOrPropertyWithValue("title", TEST_PUBLICATION_TITLE)
                .satisfies(entry -> assertThat(entry.getChildren())
                        .hasSize(1)
                        .allSatisfy(entryElement -> assertThat(entryElement)
                                .isInstanceOf(Entry.class)
                                .hasFieldOrPropertyWithValue("title", TEST_SECTION_TITLE)
                        )
                )
                .satisfies(entry -> assertThat(((Entry) entry.getChildren().get(0)).getChildren().get(0))
                        .isInstanceOf(DMRef.class)
                        .hasFieldOrPropertyWithValue("code", TEST_DM_CODE)
                        .hasFieldOrPropertyWithValue("issue", DM_ISSUE)
                        .hasFieldOrPropertyWithValue("language", DM_LANGUAGE)
                        .hasFieldOrPropertyWithValue("title", DM_TITLE)
                );
    }
}