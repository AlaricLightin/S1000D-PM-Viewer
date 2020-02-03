package ru.biderman.s1000dpmviewer.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.biderman.s1000dpmviewer.utils.TestUtils.getDataFile;

@ExtendWith(SpringExtension.class)
@DisplayName("Утилиты работы с XML ")
class XMLDocumentUtilsTest {
    @DisplayName("должны считывать XML из файла")
    @Test
    void shouldReadXMLFromFile()  {
        Document document = XMLDocumentUtils.getDocumentFromFile(getDataFile("pub1.xml"));
        assertThat(document)
                .isNotNull()
                .extracting(d -> d.getDocumentElement().getTagName())
                .isEqualTo("pm");
    }

    @DisplayName("должны считывать XML из строки")
    @Test
    void shouldReadXMLFromString() {
        Document document = XMLDocumentUtils.getDocumentFromString("<a></a>");
        assertThat(document)
                .isNotNull()
                .extracting(d -> d.getDocumentElement().getTagName())
                .isEqualTo("a");
    }

    @DisplayName("должны сохранять xml в строку")
    @Test
    void shouldWriteXMLToString() {
        Document document = XMLDocumentUtils.getDocumentFromString("<a></a>");
        assertThat(XMLDocumentUtils.getStringFromDocument(document))
                .isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><a/>");
    }
}