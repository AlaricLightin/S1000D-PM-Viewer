package ru.biderman.s1000dpmviewer.xmlparsers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import ru.biderman.s1000dpmviewer.utils.XMLDocumentUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Парсер идентификаций ")
class IdentParser4_1Test {
    @DisplayName("должен возвращать идентификационную часть для публикации")
    @Test
    void shouldParsePmIdent() {
        Document document = XMLDocumentUtils.getDocumentFromString(
                "<pmIdent>\n" +
                "<pmCode modelIdentCode=\"AA\" pmIssuer=\"00001\" pmNumber=\"00001\" pmVolume=\"00\"></pmCode>\n" +
                "<language languageIsoCode=\"ru\" countryIsoCode=\"RU\"></language>\n" +
                "<issueInfo issueNumber=\"001\" inWork=\"00\"></issueInfo>\n" +
                "</pmIdent>");

        assertThat(IdentParser4_1.createPMIdent(document.getDocumentElement()))
                .hasFieldOrPropertyWithValue("code", "AA-00001-00001-00")
                .hasFieldOrPropertyWithValue("language", "RU-RU")
                .hasFieldOrPropertyWithValue("issue", "001-00");
    }

    @DisplayName("должен возвращать идентификационную часть для МД")
    @Test
    void shouldParseDmIdent() {
        Document document = XMLDocumentUtils.getDocumentFromString(
                "<dmRefIdent>\n" +
                        "<dmCode modelIdentCode=\"TEST1\" systemDiffCode=\"A\" systemCode=\"111\" subSystemCode=\"1\" subSubSystemCode=\"1\" assyCode=\"11\" disassyCode=\"11\" disassyCodeVariant=\"A\" infoCode=\"110\" infoCodeVariant=\"A\" itemLocationCode=\"A\"></dmCode>" +
                        "<language languageIsoCode=\"ru\" countryIsoCode=\"RU\"></language>\n" +
                        "<issueInfo issueNumber=\"001\" inWork=\"00\"></issueInfo>\n" +
                        "</dmRefIdent>"
        );

        assertThat(IdentParser4_1.createDMIdent(document.getDocumentElement()))
                .hasFieldOrPropertyWithValue("code", "TEST1-A-111-11-11-11A-110A-A")
                .hasFieldOrPropertyWithValue("language", "RU-RU")
                .hasFieldOrPropertyWithValue("issue", "001-00");
    }
}